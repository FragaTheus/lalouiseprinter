package br.com.matheusfragadev.lalouise.application.print;

import br.com.matheusfragadev.lalouise.application.label.LabelService;
import br.com.matheusfragadev.lalouise.application.label.ValidityCalculatorService;
import br.com.matheusfragadev.lalouise.application.label.utils.PrintLabelCommand;
import br.com.matheusfragadev.lalouise.application.product.ProductService;
import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.application.sector.SectorService;
import br.com.matheusfragadev.lalouise.application.user.profile.registry.UserServiceRegistry;
import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import br.com.matheusfragadev.lalouise.infra.context.sector.SectorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orquestrador do fluxo completo de impressão de etiqueta:
 * validação → persistência → ZPL → fila RabbitMQ.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrintService {

    private final LabelService labelService;
    private final PrintJobService printJobService;
    private final ZplService zplService;
    private final ProductService productService;
    private final RestaurantService restaurantService;
    private final SectorService sectorService;
    private final ValidityCalculatorService validityCalculatorService;
    private final UserServiceRegistry userServiceRegistry;

    @Transactional
    public Label print(PrintLabelCommand command) {
        var restaurant = restaurantService.getRestaurant(RestaurantContext.get());
        var sector     = sectorService.getSector(SectorContext.get());

        if (!restaurant.isActive() || !sector.isActive()) {
            throw new InactiveResourceException("Não é possível imprimir em um restaurante ou setor inativo.");
        }

        var product      = productService.getProduct(command.productId());
        var validateDate = validityCalculatorService.calculate(product.getCategory(), command.storage());

        var label = new Label(
                restaurant.getId(),
                sector.getId(),
                command.productId(),
                command.userId(),
                validateDate
        );

        Label savedLabel = labelService.save(label);

        // getUserName não precisa mais do Role — CredentialsRepository (SINGLE_TABLE)
        // localiza o usuário em uma única query sem precisar saber em qual tabela buscar.
        var userNickname = userServiceRegistry.getUserName(command.userId());

        var zplCommand = PrintMapper.toZplGenerateCommand(
                savedLabel,
                restaurant.getName().value(),
                sector.getName().value(),
                product.getName().value(),
                userNickname
        );

        var zpl = zplService.generate(zplCommand, command.copies());

        // Routing key dinâmica: "print.{restaurantId}" → apenas o agente deste
        // restaurante receberá a mensagem da sua fila própria no RabbitMQ.
        printJobService.queue(zpl, command.copies(), restaurant.getId());

        return savedLabel;
    }
}

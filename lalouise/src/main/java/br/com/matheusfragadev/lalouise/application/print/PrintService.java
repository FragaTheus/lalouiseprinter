package br.com.matheusfragadev.lalouise.application.print;

import br.com.matheusfragadev.lalouise.application.label.LabelService;
import br.com.matheusfragadev.lalouise.application.label.ValidityCalculatorService;
import br.com.matheusfragadev.lalouise.application.print.utils.command.PrintLabelCommand;
import br.com.matheusfragadev.lalouise.application.print.utils.command.ReprintLabelCommand;
import br.com.matheusfragadev.lalouise.application.print.utils.mapper.PrintMapper;
import br.com.matheusfragadev.lalouise.application.print.utils.result.ValidateResult;
import br.com.matheusfragadev.lalouise.application.product.ProductService;
import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.application.sector.SectorService;
import br.com.matheusfragadev.lalouise.application.user.profile.registry.UserServiceRegistry;
import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import br.com.matheusfragadev.lalouise.domain.label.enums.Status;
import br.com.matheusfragadev.lalouise.domain.label.exceptions.InvalidLabelStateException;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductActiveException;
import br.com.matheusfragadev.lalouise.domain.restaurant.entity.Restaurant;
import br.com.matheusfragadev.lalouise.domain.sector.entity.Sector;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import br.com.matheusfragadev.lalouise.infra.context.sector.SectorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        var result = validateIfRestaurantAndSectorIsActive();

        var product = productService.getProduct(command.productId());
        if (!product.isActive()) throw new ProductActiveException("Não é possível imprimir etiqueta de um produto inativo.");

        var validateDate = validityCalculatorService.calculate(product.getCategory(), command.storage());

        var label = new Label(
                result.restaurant().getId(),
                result.sector().getId(),
                command.productId(),
                command.userId(),
                validateDate
        );

        Label savedLabel = labelService.save(label);

        var userNickname = userServiceRegistry.getUserName(command.userId());

        var zplCommand = PrintMapper.toZplGenerateCommand(
                savedLabel,
                result.restaurant().getName().value(),
                result.sector().getName().value(),
                product.getName().value(),
                userNickname
        );

        var zpl = zplService.generate(zplCommand, command.copies());

        printJobService.queue(zpl, command.copies(), result.restaurant().getId());

        return savedLabel;
    }

    @Transactional
    public Label reprint(ReprintLabelCommand command) {
        var result = validateIfRestaurantAndSectorIsActive();

        var original = labelService.getLabel(command.currentLabelId());

        var product = productService.getProduct(original.getProductId());
        var validateDate = validityCalculatorService.calculate(product.getCategory(), command.storage());

        log.info("Reprinting label {} in sector {} by user {}", command.currentLabelId(), result.sector().getId(), command.userId());

        var reprinted = labelService.save(original.reprint(result.sector().getId(), command.userId(), validateDate));

        var zplGenerateCommand = PrintMapper.toZplGenerateCommand(
                reprinted,
                result.restaurant().getName().value(),
                result.sector().getName().value(),
                product.getName().value(),
                userServiceRegistry.getUserName(command.userId())
        );

        var zpl = zplService.generate(zplGenerateCommand, command.copies());

        printJobService.queue(zpl, command.copies(), result.restaurant().getId());

        return reprinted;
    }

    private ValidateResult validateIfRestaurantAndSectorIsActive(){
        var restaurant = restaurantService.getRestaurant(RestaurantContext.get());
        var sector = sectorService.getSector(SectorContext.get());

        if (!restaurant.isActive() || !sector.isActive()) {
            throw new InactiveResourceException("Não é possível imprimir em um restaurante ou setor inativo.");
        }

        return new ValidateResult(restaurant, sector);
    }

}


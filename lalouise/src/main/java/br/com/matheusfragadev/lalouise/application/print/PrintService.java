package br.com.matheusfragadev.lalouise.application.print;

import br.com.matheusfragadev.lalouise.application.label.LabelService;
import br.com.matheusfragadev.lalouise.application.label.ValidityCalculatorService;
import br.com.matheusfragadev.lalouise.application.print.utils.command.PrintLabelCommand;
import br.com.matheusfragadev.lalouise.application.print.utils.command.GeneraleLabelForNewLocationCommand;
import br.com.matheusfragadev.lalouise.application.print.utils.command.ReprintLabelCommand;
import br.com.matheusfragadev.lalouise.application.print.utils.command.ZplGenerateCommand;
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
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import br.com.matheusfragadev.lalouise.infra.context.sector.SectorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

        sendToPrintJob(command.copies(), zplCommand, result.restaurant().getId());

        return savedLabel;
    }

    @Transactional
    public Label generateLabelForNewLocation(GeneraleLabelForNewLocationCommand command) {
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

        sendToPrintJob(command.copies(), zplGenerateCommand, result.restaurant().getId());

        return reprinted;
    }

    @Transactional
    public Label reprint(ReprintLabelCommand command){
        var label = labelService.getLabel(command.labelId());
        if (label.getStatus().equals(Status.DISCARDED) || label.getStatus().equals(Status.EXPIRED)) {
            throw new InvalidLabelStateException("Não é possível reimprimir uma etiqueta descartada ou vencida.");
        }
        var result = validateIfRestaurantAndSectorIsActive();
        var product = productService.getProduct(label.getProductId());
        var zplGenerateCommand = PrintMapper.toZplGenerateCommand(
                label,
                result.restaurant().getName().value(),
                result.sector().getName().value(),
                product.getName().value(),
                userServiceRegistry.getUserName(command.userId())
        );

        sendToPrintJob(command.copies(), zplGenerateCommand, result.restaurant().getId());

        return label;
    }

    //Metodos auxiliares

    private void sendToPrintJob(int copies, ZplGenerateCommand command, UUID restaurantId){
        var resolvedCopies = normalizeCopies(copies);
        printJobService.queue(zplService.generate(command, resolvedCopies), resolvedCopies, restaurantId);
    }

    private int normalizeCopies(Integer copies){
        if (copies == null || copies <= 0) {
            return 1;
        }
        return copies;
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


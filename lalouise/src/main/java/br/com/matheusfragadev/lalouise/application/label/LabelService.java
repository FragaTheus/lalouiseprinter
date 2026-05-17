package br.com.matheusfragadev.lalouise.application.label;

import br.com.matheusfragadev.lalouise.application.label.utils.PrintLabelCommand;
import br.com.matheusfragadev.lalouise.application.label.utils.ReprintLabelByContextCommand;
import br.com.matheusfragadev.lalouise.application.label.utils.ReprintLabelByInputCommand;
import br.com.matheusfragadev.lalouise.application.product.ProductService;
import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.application.sector.SectorService;
import br.com.matheusfragadev.lalouise.application.user.UserService;
import br.com.matheusfragadev.lalouise.application.user.profile.registry.UserServiceRegistry;
import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import br.com.matheusfragadev.lalouise.domain.label.exceptions.LabelNotFoundException;
import br.com.matheusfragadev.lalouise.domain.label.repository.LabelRepository;
import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import br.com.matheusfragadev.lalouise.infra.context.sector.SectorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final ProductService productService;
    private final ValidityCalculatorService validityCalculatorService;
    private final RestaurantService restaurantService;
    private final SectorService sectorService;
    private final UserServiceRegistry userServiceRegistry;

    @Transactional
    public Label print(PrintLabelCommand command) {
        var restaurantId = RestaurantContext.get();
        var sectorId = SectorContext.get();
        log.info("Setor do id {}", sectorId);
        var restaurant = restaurantService.getRestaurant(restaurantId);
        var sector = sectorService.getSector(sectorId);

        if (!restaurant.isActive()) {
            throw new InactiveResourceException("Não é possível imprimir etiqueta em um restaurante inativo.");
        }
        if (!sector.isActive()) {
            throw new InactiveResourceException("Não é possível imprimir etiqueta em um setor inativo.");
        }

        log.info("Printing label for product {} in sector {}", command.productId(), sectorId);

        var product = productService.getProduct(command.productId());
        var validateDate = validityCalculatorService.calculate(product.getCategory(), command.storage());

        var label = new Label(
                restaurantId,
                sectorId,
                command.productId(),
                command.userId(),
                validateDate
        );

        log.info("Label printed successfully for product {} with validateDate {}", command.productId(), validateDate);
        return labelRepository.save(label);
    }

    @Transactional(readOnly = true)
    public Label getLabel(UUID id) {
        var restaurantId = RestaurantContext.get();
        return labelRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new LabelNotFoundException("Etiqueta não encontrada com id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Label> getAllBySector(String term, Pageable pageable) {
        var restaurantId = RestaurantContext.get();
        var sectorId = SectorContext.get();
        return labelRepository.findAllLabels(restaurantId, sectorId, term, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Label> getAllByRestaurant(String term, Pageable pageable) {
        var restaurantId = RestaurantContext.get();
        return labelRepository.findAllLabelsByRestaurant(restaurantId, term, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Label> getAllByLot(String lotCode, Pageable pageable) {
        var restaurantId = RestaurantContext.get();
        return labelRepository.findAllByRestaurantIdAndLotCode(restaurantId, lotCode, pageable);
    }


    @Transactional
    public Label reprintBySectorContext(ReprintLabelByContextCommand command) {
        var sectorId = SectorContext.get();
        return reprint(command.currentLabelId(), command.userId(), sectorId, command.storage());
    }

    @Transactional
    public Label reprintByInputSector(ReprintLabelByInputCommand command){
        return reprint(command.currentLabelId(), command.userId(), command.sectorId(), command.storage());
    }

    public String getRestaurantName(UUID targetId){
        var label = getLabel(targetId);
        var restaurant = restaurantService.getRestaurant(label.getRestaurantId());
        return restaurant.getName().value();
    }

    public String getSectorName(UUID targetId){
        var label = getLabel(targetId);
        var sector = sectorService.getSector(label.getSectorId());
        return sector.getName().value();
    }

    public String getWhoPrinted(UUID targetId){
        var label = getLabel(targetId);
        return userServiceRegistry.getUserName(label.getUserId());
    }


    private Label reprint(
            UUID currentLabelId,
            UUID userId,
            UUID sectorId,
            Storage storage
    ){
        var restaurantId = RestaurantContext.get();

        var restaurant = restaurantService.getRestaurant(restaurantId);
        var sector = sectorService.getSector(sectorId);

        if (!restaurant.isActive()) {
            throw new InactiveResourceException("Não é possível reimprimir etiqueta em um restaurante inativo.");
        }
        if (!sector.isActive()) {
            throw new InactiveResourceException("Não é possível reimprimir etiqueta em um setor inativo.");
        }

        var original = getLabel(currentLabelId);
        var product = productService.getProduct(original.getProductId());
        var validateDate = validityCalculatorService.calculate(product.getCategory(), storage);

        log.info("Reprinting label {} in sector {} by user {}", currentLabelId, sectorId, userId);
        var reprinted = original.reprint(sectorId, userId, validateDate);
        return labelRepository.save(reprinted);
    }
}

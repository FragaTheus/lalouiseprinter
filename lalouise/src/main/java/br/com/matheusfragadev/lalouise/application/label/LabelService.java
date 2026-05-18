package br.com.matheusfragadev.lalouise.application.label;

import br.com.matheusfragadev.lalouise.application.label.utils.PrintLabelCommand;
import br.com.matheusfragadev.lalouise.application.label.utils.ReprintLabelByContextCommand;
import br.com.matheusfragadev.lalouise.application.label.utils.ReprintLabelByInputCommand;
import br.com.matheusfragadev.lalouise.application.print.PrintJobService;
import br.com.matheusfragadev.lalouise.application.print.ZplService;
import br.com.matheusfragadev.lalouise.application.product.ProductService;
import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.application.sector.SectorService;
import br.com.matheusfragadev.lalouise.application.user.profile.registry.UserServiceRegistry;
import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import br.com.matheusfragadev.lalouise.domain.label.exceptions.LabelNotFoundException;
import br.com.matheusfragadev.lalouise.domain.label.repository.LabelRepository;
import br.com.matheusfragadev.lalouise.domain.sector.enums.Storage;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import br.com.matheusfragadev.lalouise.infra.context.sector.SectorContext;
import br.com.matheusfragadev.lalouise.infra.controller.label.utils.resolver.LabelInfoResolverResult;
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

    @Transactional
    public Label save(Label label){
        return labelRepository.saveAndFlush(label);
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
        var sectorId     = SectorContext.get();
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

//    @Transactional
//    public Label reprintBySectorContext(ReprintLabelByContextCommand command) {
//        var sectorId = SectorContext.get();
//        return reprint(command.currentLabelId(), command.userId(), sectorId, command.storage(), 1);
//    }
//
//    @Transactional
//    public Label reprintByInputSector(ReprintLabelByInputCommand command) {
//        return reprint(command.currentLabelId(), command.userId(), command.sectorId(), command.storage(), 1);
//    }

//    private Label reprint(
//            UUID currentLabelId,
//            UUID userId,
//            UUID sectorId,
//            Storage storage,
//            int copies
//    ) {
//        var restaurantId = RestaurantContext.get();
//
//        var restaurant = restaurantService.getRestaurant(restaurantId);
//        var sector     = sectorService.getSector(sectorId);
//
//        if (!restaurant.isActive()) {
//            throw new InactiveResourceException("Não é possível reimprimir etiqueta em um restaurante inativo.");
//        }
//        if (!sector.isActive()) {
//            throw new InactiveResourceException("Não é possível reimprimir etiqueta em um setor inativo.");
//        }
//
//        var original     = getLabel(currentLabelId);
//        var product      = productService.getProduct(original.getProductId());
//        var validateDate = validityCalculatorService.calculate(product.getCategory(), storage);
//
//        log.info("Reprinting label {} in sector {} by user {}", currentLabelId, sectorId, userId);
//
//        var reprinted = labelRepository.save(original.reprint(sectorId, userId, validateDate));
//
//        var resolverResult = LabelInfoResolverResult.builder()
//                .label(reprinted)
//                .restaurantName(restaurant.getName().value())
//                .sectorName(sector.getName().value())
//                .productName(product.getName().value())
//                .printedByName(userServiceRegistry.getUserName(userId))
//                .build();
//
//        var zpl = zplService.generate(resolverResult, copies);
//
//        printJobService.queue(zpl, copies, restaurantId);
//
//        return reprinted;
//    }
}

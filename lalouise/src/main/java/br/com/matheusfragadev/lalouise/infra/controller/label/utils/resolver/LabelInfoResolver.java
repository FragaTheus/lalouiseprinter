package br.com.matheusfragadev.lalouise.infra.controller.label.utils.resolver;

import br.com.matheusfragadev.lalouise.application.product.ProductService;
import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.application.sector.SectorService;
import br.com.matheusfragadev.lalouise.application.user.UserService;
import br.com.matheusfragadev.lalouise.application.user.profile.registry.UserServiceRegistry;
import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LabelInfoResolver {

    private final RestaurantService restaurantService;
    private final SectorService sectorService;
    private final UserServiceRegistry userServiceRegistry;
    private final ProductService productService;

    public LabelInfoResolverResult resolver(Label label){
        var restaurantName = restaurantService.getRestaurant(label.getRestaurantId()).getName().value();
        var sectorName = sectorService.getSector(label.getSectorId()).getName().value();
        var printedByName = userServiceRegistry.getUserName(label.getUserId());
        var productName = productService.getProduct(label.getProductId()).getName().value();

        return LabelInfoResolverResult.builder()
                .label(label)
                .restaurantName(restaurantName)
                .sectorName(sectorName)
                .productName(productName)
                .printerdByName(printedByName)
                .build();
    }

}

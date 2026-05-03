package br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.mapper;

import br.com.matheusfragadev.lalouise.domain.restaurant.entity.Restaurant;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto.RestaurantInfo;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto.RestaurantSummary;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RestaurantMapper {

    public static RestaurantSummary toRestaurantSummary(Restaurant restaurant){
        return RestaurantSummary.builder()
                .restaurantId(restaurant.getId())
                .restaurantName(restaurant.getName().value())
                .active(restaurant.isActive())
                .build();
    }

    public static RestaurantInfo toRestaurantInfo(Restaurant restaurant){
        return RestaurantInfo.builder()
                .id(restaurant.getId())
                .name(restaurant.getName().value())
                .cnpj(restaurant.getCnpj().value())
                .active(restaurant.isActive())
                .createdAt(restaurant.getCreatedAt())
                .updatedAt(restaurant.getUpdatedAt())
                .build();
    }

}

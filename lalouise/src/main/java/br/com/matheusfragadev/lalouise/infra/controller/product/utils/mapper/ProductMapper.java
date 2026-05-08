package br.com.matheusfragadev.lalouise.infra.controller.product.utils.mapper;

import br.com.matheusfragadev.lalouise.domain.product.entity.Product;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response.ProductInfo;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response.ProductSummary;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductMapper {

    public static ProductSummary toProductSummary(Product product) {
        return ProductSummary.builder()
                .id(product.getId())
                .name(product.getName().value())
                .description(product.getDescription().value())
                .active(product.isActive())
                .restaurantId(product.getRestaurantId())
                .build();
    }

    public static ProductInfo toProductInfo(Product product) {
        return ProductInfo.builder()
                .id(product.getId())
                .name(product.getName().value())
                .description(product.getDescription().value())
                .active(product.isActive())
                .restaurantId(product.getRestaurantId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

}


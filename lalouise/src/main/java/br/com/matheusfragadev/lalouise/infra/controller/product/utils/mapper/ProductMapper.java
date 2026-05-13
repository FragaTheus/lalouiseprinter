package br.com.matheusfragadev.lalouise.infra.controller.product.utils.mapper;

import br.com.matheusfragadev.lalouise.domain.product.entity.Product;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response.ProductInfo;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response.ProductLookup;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response.ProductSummary;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductMapper {

    public static ProductSummary toProductSummary(Product product) {
        return ProductSummary.builder()
                .id(product.getId())
                .name(product.getName().value())
                .active(product.isActive())
                .build();
    }

    public static ProductInfo toProductInfo(Product product) {
        return ProductInfo.builder()
                .id(product.getId())
                .name(product.getName().value())
                .category(product.getCategory())
                .active(product.isActive())
                .restaurantId(product.getRestaurantId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static ProductLookup toLookup(Product product){
        return new ProductLookup(product.getId(), product.getName().value());
    }

}


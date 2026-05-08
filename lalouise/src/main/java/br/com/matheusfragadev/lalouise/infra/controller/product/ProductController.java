package br.com.matheusfragadev.lalouise.infra.controller.product;

import br.com.matheusfragadev.lalouise.application.product.ProductService;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.request.CreateProductRequest;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.request.ProductChangeDescriptionRequest;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.request.ProductChangeNameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response.ProductInfo;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response.ProductSummary;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.mapper.ProductMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductSummary>> list(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) Boolean active,
            @PageableDefault Pageable pageable
    ) {
        var products = productService.getAll(term, active, pageable);
        return ResponseEntity.ok(products.map(ProductMapper::toProductSummary));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductInfo> info(@PathVariable UUID productId) {
        var product = productService.getProduct(productId);
        return ResponseEntity.ok(ProductMapper.toProductInfo(product));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<String> create(@Valid @RequestBody CreateProductRequest request) {
        var product = productService.createProduct(request.name(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(product.getId().toString());
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{productId}/change-name")
    public ResponseEntity<Void> updateName(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductChangeNameRequest request
    ){
        productService.changeName(productId, request.newProductName());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{productId}/change-description")
    public ResponseEntity<Void> updateDescription(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductChangeDescriptionRequest request
    ){
        productService.changeDescription(productId, request.newDescription());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Void> deactivate(@PathVariable UUID productId) {
        productService.deactivate(productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{productId}/reactivate")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<Void> reactivate(@PathVariable UUID productId) {
        productService.reactivate(productId);
        return ResponseEntity.noContent().build();
    }
}


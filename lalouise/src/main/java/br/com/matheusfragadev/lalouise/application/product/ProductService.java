package br.com.matheusfragadev.lalouise.application.product;

import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.domain.product.entity.Product;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductAlreadyExistsException;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductNotFoundException;
import br.com.matheusfragadev.lalouise.domain.product.repository.ProductRepository;
import br.com.matheusfragadev.lalouise.domain.product.vo.ProductDescription;
import br.com.matheusfragadev.lalouise.domain.product.vo.ProductName;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
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
public class ProductService {

    private final ProductRepository productRepository;
    private final RestaurantService restaurantService;

    @Transactional
    public Product createProduct(String name, String description) {
        try {
            var restaurantId = RestaurantContext.get();
            log.info("Creating product for restaurant: {}", restaurantId);

            var restaurant = restaurantService.getRestaurant(restaurantId);
            if (!restaurant.isActive()) {
                throw new InactiveResourceException("Não é possível criar produto em um restaurante inativo.");
            }

            var productName = new ProductName(name);
            verifyIfNameIsThemSameInRestaurantContext(productName.value(), restaurantId);

            var product = new Product(productName, new ProductDescription(description), restaurantId);
            log.info("Product created successfully for restaurant: {}", restaurantId);
            return productRepository.save(product);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public Product changeName(UUID targetId, String newProductName){
        var restaurantId = RestaurantContext.get();
        verifyIfNameIsThemSameInRestaurantContext(newProductName, restaurantId);
        var product = getProduct(targetId);
        if (newProductName.equals(product.getName().value())) {
            return null;
        }
        var name = new ProductName(newProductName);
        product.changeName(name);
        return productRepository.save(product);
    }

    @Transactional
    public Product changeDescription(UUID targetId, String newProductDescription){
        var product = getProduct(targetId);
        if (newProductDescription.equals(product.getDescription().value())) {
            return null;
        }
        var description = new ProductDescription(newProductDescription);
        product.changeDescription(description);
        return productRepository.save(product);
    }

    @Transactional
    public Product deactivate(UUID id) {
        try {
            log.info("Deactivating product with id: {}", id);
            var product = getProduct(id);
            product.deactivate();
            log.info("Product with id {} deactivated successfully", id);
            return productRepository.save(product);
        } catch (Exception e) {
            log.error("Error deactivating product with id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public Product reactivate(UUID id) {
        try {
            log.info("Reactivating product with id: {}", id);
            var product = getProduct(id);
            product.reactivate();
            log.info("Product with id {} reactivated successfully", id);
            return productRepository.save(product);
        } catch (Exception e) {
            log.error("Error reactivating product with id {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Product getProduct(UUID id) {
        var restaurantId = RestaurantContext.get();
        return productRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado com id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Product> getAll(String term, Boolean active, Pageable pageable) {
        var restaurantId = RestaurantContext.get();
        return productRepository.findAllProducts(term, active, restaurantId, pageable);
    }

    private void verifyIfNameIsThemSameInRestaurantContext(String nameValue, UUID restaurantId){
        if (productRepository.existsByNameValueAndRestaurantId(nameValue, restaurantId)) {
            throw new ProductAlreadyExistsException("Já existe um produto com esse nome neste restaurante.");
        }
    }
}


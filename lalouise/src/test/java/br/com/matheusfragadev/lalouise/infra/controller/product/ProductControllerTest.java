package br.com.matheusfragadev.lalouise.infra.controller.product;
import br.com.matheusfragadev.lalouise.application.product.ProductService;
import br.com.matheusfragadev.lalouise.domain.product.entity.Product;
import br.com.matheusfragadev.lalouise.domain.product.enums.Category;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductActiveException;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductAlreadyExistsException;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductNotFoundException;
import br.com.matheusfragadev.lalouise.domain.product.vo.ProductName;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.request.ChangeCategoryRequest;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.request.CreateProductRequest;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.request.ProductChangeNameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response.ProductInfo;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response.ProductSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    @Mock private ProductService productService;
    @InjectMocks private ProductController controller;
    @Test
    void listShouldReturn200WithMappedSummaries() {
        Product p1 = mock(Product.class);
        Product p2 = mock(Product.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        when(p1.getId()).thenReturn(id1);
        when(p1.getName()).thenReturn(new ProductName("Frango Grelhado"));
        when(p1.isActive()).thenReturn(true);
        when(p2.getId()).thenReturn(id2);
        when(p2.getName()).thenReturn(new ProductName("Peixe Assado"));
        when(p2.isActive()).thenReturn(false);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(p1, p2), pageable, 2);
        when(productService.getAll(null, null, pageable)).thenReturn(page);
        ResponseEntity<Page<ProductSummary>> response = controller.list(null, null, pageable);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(List.of(
                new ProductSummary(id1, "Frango Grelhado", true),
                new ProductSummary(id2, "Peixe Assado", false)
        ), response.getBody().getContent());
    }
    @Test
    void listShouldForwardTermAndActiveToService() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(productService.getAll("frango", true, pageable)).thenReturn(emptyPage);
        ResponseEntity<Page<ProductSummary>> response = controller.list("frango", true, pageable);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).getAll("frango", true, pageable);
    }
    @Test
    void infoShouldReturn200WithMappedProductInfo() {
        UUID productId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Instant now = Instant.parse("2026-05-15T10:15:30Z");
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(productId);
        when(product.getName()).thenReturn(new ProductName("Frango Grelhado"));
        when(product.getCategory()).thenReturn(Category.PROTEIN);
        when(product.isActive()).thenReturn(true);
        when(product.getRestaurantId()).thenReturn(restaurantId);
        when(product.getCreatedAt()).thenReturn(now);
        when(product.getUpdatedAt()).thenReturn(now);
        when(productService.getProduct(productId)).thenReturn(product);
        ResponseEntity<ProductInfo> response = controller.info(productId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new ProductInfo(productId, "Frango Grelhado", Category.PROTEIN, true, restaurantId, now, now), response.getBody());
    }
    @Test
    void infoShouldPropagateExceptionWhenProductNotFound() {
        UUID productId = UUID.randomUUID();
        when(productService.getProduct(productId))
                .thenThrow(new ProductNotFoundException("Produto não encontrado com id: " + productId));
        ProductNotFoundException ex = assertThrows(ProductNotFoundException.class, () -> controller.info(productId));
        assertTrue(ex.getMessage().contains("Produto não encontrado com id"));
    }
    @Test
    void createShouldReturn201WithProductId() {
        UUID id = UUID.randomUUID();
        CreateProductRequest request = new CreateProductRequest("Frango Grelhado", Category.PROTEIN);
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(id);
        when(productService.createProduct("Frango Grelhado", Category.PROTEIN)).thenReturn(product);
        ResponseEntity<String> response = controller.create(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(id.toString(), response.getBody());
        verify(productService).createProduct("Frango Grelhado", Category.PROTEIN);
    }
    @Test
    void createShouldPropagateExceptionWhenNameAlreadyExists() {
        CreateProductRequest request = new CreateProductRequest("Frango Grelhado", Category.PROTEIN);
        when(productService.createProduct("Frango Grelhado", Category.PROTEIN))
                .thenThrow(new ProductAlreadyExistsException("Já existe um produto com esse nome neste restaurante."));
        ProductAlreadyExistsException ex = assertThrows(ProductAlreadyExistsException.class, () -> controller.create(request));
        assertEquals("Já existe um produto com esse nome neste restaurante.", ex.getMessage());
    }
    @Test
    void createShouldPropagateExceptionWhenRestaurantIsInactive() {
        CreateProductRequest request = new CreateProductRequest("Frango Grelhado", Category.PROTEIN);
        when(productService.createProduct("Frango Grelhado", Category.PROTEIN))
                .thenThrow(new InactiveResourceException("Não é possível criar produto em um restaurante inativo."));
        assertThrows(InactiveResourceException.class, () -> controller.create(request));
    }
    @Test
    void updateNameShouldReturn204NoContent() {
        UUID productId = UUID.randomUUID();
        ProductChangeNameRequest request = new ProductChangeNameRequest("Frango Assado");
        when(productService.changeName(productId, "Frango Assado")).thenReturn(mock(Product.class));
        ResponseEntity<Void> response = controller.updateName(productId, request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(productService).changeName(productId, "Frango Assado");
    }
    @Test
    void updateNameShouldPropagateExceptionWhenNameAlreadyExists() {
        UUID productId = UUID.randomUUID();
        ProductChangeNameRequest request = new ProductChangeNameRequest("Frango Assado");
        when(productService.changeName(productId, "Frango Assado"))
                .thenThrow(new ProductAlreadyExistsException("Já existe um produto com esse nome neste restaurante."));
        assertThrows(ProductAlreadyExistsException.class, () -> controller.updateName(productId, request));
    }
    @Test
    void updateCategoryShouldReturn204NoContent() {
        UUID productId = UUID.randomUUID();
        ChangeCategoryRequest request = new ChangeCategoryRequest(Category.SEAFOOD);
        when(productService.changeCategory(productId, Category.SEAFOOD)).thenReturn(mock(Product.class));
        ResponseEntity<Void> response = controller.updateCategory(productId, request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(productService).changeCategory(productId, Category.SEAFOOD);
    }
    @Test
    void updateCategoryShouldPropagateExceptionWhenProductNotFound() {
        UUID productId = UUID.randomUUID();
        ChangeCategoryRequest request = new ChangeCategoryRequest(Category.SEAFOOD);
        when(productService.changeCategory(productId, Category.SEAFOOD))
                .thenThrow(new ProductNotFoundException("Produto não encontrado com id: " + productId));
        assertThrows(ProductNotFoundException.class, () -> controller.updateCategory(productId, request));
    }
    @Test
    void deactivateShouldReturn204NoContent() {
        UUID productId = UUID.randomUUID();
        when(productService.deactivate(productId)).thenReturn(mock(Product.class));
        ResponseEntity<Void> response = controller.deactivate(productId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(productService).deactivate(productId);
    }
    @Test
    void reactivateShouldReturn204NoContent() {
        UUID productId = UUID.randomUUID();
        when(productService.reactivate(productId)).thenReturn(mock(Product.class));
        ResponseEntity<Void> response = controller.reactivate(productId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(productService).reactivate(productId);
    }
    @Test
    void reactivateShouldPropagateExceptionWhenAlreadyActive() {
        UUID productId = UUID.randomUUID();
        when(productService.reactivate(productId))
                .thenThrow(new ProductActiveException("Produto já está ativo"));
        assertThrows(ProductActiveException.class, () -> controller.reactivate(productId));
    }
}

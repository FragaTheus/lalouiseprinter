package br.com.matheusfragadev.lalouise.infra.controller.product;
import br.com.matheusfragadev.lalouise.application.product.ProductService;
import br.com.matheusfragadev.lalouise.domain.product.entity.Product;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductActiveException;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductAlreadyExistsException;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductNotFoundException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.request.CreateProductRequest;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.request.ProductChangeNameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response.ProductInfo;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.dto.response.ProductSummary;
import br.com.matheusfragadev.lalouise.infra.controller.product.utils.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
    // ── list ──────────────────────────────────────────────────────────────────
    @Test
    void listShouldReturn200WithMappedSummaries() {
        UUID restaurantId = UUID.randomUUID();
        Product p1 = mock(Product.class);
        Product p2 = mock(Product.class);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        ProductSummary s1 = new ProductSummary(id1, "Frango Grelhado", "Prato saboroso", true, restaurantId);
        ProductSummary s2 = new ProductSummary(id2, "Peixe Assado", "Prato leve", true, restaurantId);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(p1, p2), pageable, 2);
        when(productService.getAll(null, null, pageable)).thenReturn(page);
        try (MockedStatic<ProductMapper> mapper = mockStatic(ProductMapper.class)) {
            mapper.when(() -> ProductMapper.toProductSummary(p1)).thenReturn(s1);
            mapper.when(() -> ProductMapper.toProductSummary(p2)).thenReturn(s2);
            ResponseEntity<Page<ProductSummary>> response = controller.list(null, null, pageable);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().getContent().size());
            assertSame(s1, response.getBody().getContent().get(0));
            assertSame(s2, response.getBody().getContent().get(1));
        }
    }
    @Test
    void listShouldReturnEmptyPageWhenNoProductsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(productService.getAll(null, null, pageable)).thenReturn(emptyPage);
        ResponseEntity<Page<ProductSummary>> response = controller.list(null, null, pageable);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
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
    // ── info ──────────────────────────────────────────────────────────────────
    @Test
    void infoShouldReturn200WithMappedProductInfo() {
        UUID productId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Instant now = Instant.now();
        Product product = mock(Product.class);
        ProductInfo info = ProductInfo.builder()
                .id(productId).name("Frango Grelhado").description("Prato saboroso")
                .active(true).restaurantId(restaurantId).createdAt(now).updatedAt(now)
                .build();
        when(productService.getProduct(productId)).thenReturn(product);
        try (MockedStatic<ProductMapper> mapper = mockStatic(ProductMapper.class)) {
            mapper.when(() -> ProductMapper.toProductInfo(product)).thenReturn(info);
            ResponseEntity<ProductInfo> response = controller.info(productId);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(info, response.getBody());
            verify(productService).getProduct(productId);
        }
    }
    @Test
    void infoShouldPropagateExceptionWhenProductNotFound() {
        UUID productId = UUID.randomUUID();
        when(productService.getProduct(productId))
                .thenThrow(new ProductNotFoundException("Produto não encontrado com id: " + productId));
        ProductNotFoundException ex = assertThrows(ProductNotFoundException.class, () -> controller.info(productId));
        assertTrue(ex.getMessage().contains("Produto não encontrado com id"));
    }
    // ── create ────────────────────────────────────────────────────────────────
    @Test
    void createShouldReturn201WithProductId() {
        UUID id = UUID.randomUUID();
        CreateProductRequest request = new CreateProductRequest("Frango Grelhado", "Prato saboroso");
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(id);
        when(productService.createProduct("Frango Grelhado", "Prato saboroso")).thenReturn(product);
        ResponseEntity<String> response = controller.create(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(id.toString(), response.getBody());
        verify(productService).createProduct("Frango Grelhado", "Prato saboroso");
    }
    @Test
    void createShouldPropagateExceptionWhenNameAlreadyExists() {
        CreateProductRequest request = new CreateProductRequest("Frango Grelhado", "Prato saboroso");
        when(productService.createProduct("Frango Grelhado", "Prato saboroso"))
                .thenThrow(new ProductAlreadyExistsException("Já existe um produto com esse nome neste restaurante."));
        ProductAlreadyExistsException ex = assertThrows(
                ProductAlreadyExistsException.class, () -> controller.create(request));
        assertEquals("Já existe um produto com esse nome neste restaurante.", ex.getMessage());
    }
    @Test
    void createShouldPropagateExceptionWhenRestaurantIsInactive() {
        CreateProductRequest request = new CreateProductRequest("Frango Grelhado", "Prato saboroso");
        when(productService.createProduct("Frango Grelhado", "Prato saboroso"))
                .thenThrow(new InactiveResourceException("Não é possível criar produto em um restaurante inativo."));
        assertThrows(InactiveResourceException.class, () -> controller.create(request));
    }
    // ── updateName ────────────────────────────────────────────────────────────
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
    void updateNameShouldPropagateExceptionWhenProductNotFound() {
        UUID productId = UUID.randomUUID();
        ProductChangeNameRequest request = new ProductChangeNameRequest("Frango Assado");
        when(productService.changeName(productId, "Frango Assado"))
                .thenThrow(new ProductNotFoundException("Produto não encontrado com id: " + productId));
        assertThrows(ProductNotFoundException.class, () -> controller.updateName(productId, request));
    }
    // ── updateDescription ─────────────────────────────────────────────────────
    @Test
    void updateDescriptionShouldReturn204NoContent() {
        UUID productId = UUID.randomUUID();
        ProductChangeDescriptionRequest request = new ProductChangeDescriptionRequest("Nova descricao do prato");
        when(productService.changeDescription(productId, "Nova descricao do prato")).thenReturn(mock(Product.class));
        ResponseEntity<Void> response = controller.updateDescription(productId, request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(productService).changeDescription(productId, "Nova descricao do prato");
    }
    @Test
    void updateDescriptionShouldPropagateExceptionWhenProductNotFound() {
        UUID productId = UUID.randomUUID();
        ProductChangeDescriptionRequest request = new ProductChangeDescriptionRequest("Nova descricao do prato");
        when(productService.changeDescription(productId, "Nova descricao do prato"))
                .thenThrow(new ProductNotFoundException("Produto não encontrado com id: " + productId));
        assertThrows(ProductNotFoundException.class, () -> controller.updateDescription(productId, request));
    }
    // ── deactivate ────────────────────────────────────────────────────────────
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
    void deactivateShouldPropagateExceptionWhenProductNotFound() {
        UUID productId = UUID.randomUUID();
        when(productService.deactivate(productId))
                .thenThrow(new ProductNotFoundException("Produto não encontrado com id: " + productId));
        assertThrows(ProductNotFoundException.class, () -> controller.deactivate(productId));
    }
    @Test
    void deactivateShouldPropagateExceptionWhenAlreadyInactive() {
        UUID productId = UUID.randomUUID();
        when(productService.deactivate(productId))
                .thenThrow(new ProductActiveException("Produto já está inativo"));
        assertThrows(ProductActiveException.class, () -> controller.deactivate(productId));
    }
    // ── reactivate ────────────────────────────────────────────────────────────
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
    void reactivateShouldPropagateExceptionWhenProductNotFound() {
        UUID productId = UUID.randomUUID();
        when(productService.reactivate(productId))
                .thenThrow(new ProductNotFoundException("Produto não encontrado com id: " + productId));
        assertThrows(ProductNotFoundException.class, () -> controller.reactivate(productId));
    }
    @Test
    void reactivateShouldPropagateExceptionWhenAlreadyActive() {
        UUID productId = UUID.randomUUID();
        when(productService.reactivate(productId))
                .thenThrow(new ProductActiveException("Produto já está ativo"));
        assertThrows(ProductActiveException.class, () -> controller.reactivate(productId));
    }
}

package br.com.matheusfragadev.lalouise.application.product;
import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.domain.product.entity.Product;
import br.com.matheusfragadev.lalouise.domain.product.enums.Category;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductAlreadyExistsException;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductActiveException;
import br.com.matheusfragadev.lalouise.domain.product.exception.ProductNotFoundException;
import br.com.matheusfragadev.lalouise.domain.product.repository.ProductRepository;
import br.com.matheusfragadev.lalouise.domain.product.vo.ProductName;
import br.com.matheusfragadev.lalouise.domain.restaurant.entity.Restaurant;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock private ProductRepository productRepository;
    @Mock private RestaurantService restaurantService;
    @InjectMocks private ProductService service;
    private UUID restaurantId;
    @BeforeEach
    void setupContext() {
        restaurantId = UUID.randomUUID();
        RestaurantContext.set(restaurantId);
    }
    @AfterEach
    void clearContext() {
        RestaurantContext.clear();
    }
    @Test
    void createProductShouldSaveAndReturnProductWhenInputIsValid() {
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.isActive()).thenReturn(true);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(productRepository.existsByNameValueAndRestaurantId("Frango Grelhado", restaurantId)).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        Product result = service.createProduct("Frango Grelhado", Category.PROTEIN);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("Frango Grelhado", result.getName().value()),
                () -> assertEquals(Category.PROTEIN, result.getCategory()),
                () -> assertEquals(restaurantId, result.getRestaurantId()),
                () -> assertTrue(result.isActive())
        );
        verify(productRepository).save(any(Product.class));
    }
    @Test
    void createProductShouldThrowWhenRestaurantIsInactive() {
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.isActive()).thenReturn(false);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        InactiveResourceException ex = assertThrows(
                InactiveResourceException.class,
                () -> service.createProduct("Frango Grelhado", Category.PROTEIN)
        );
        assertEquals("Não é possível criar produto em um restaurante inativo.", ex.getMessage());
        verify(productRepository, never()).save(any());
    }
    @Test
    void createProductShouldThrowWhenNameAlreadyExistsInRestaurant() {
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurant.isActive()).thenReturn(true);
        when(restaurantService.getRestaurant(restaurantId)).thenReturn(restaurant);
        when(productRepository.existsByNameValueAndRestaurantId("Frango Grelhado", restaurantId)).thenReturn(true);
        ProductAlreadyExistsException ex = assertThrows(
                ProductAlreadyExistsException.class,
                () -> service.createProduct("Frango Grelhado", Category.PROTEIN)
        );
        assertEquals("Já existe um produto com esse nome neste restaurante.", ex.getMessage());
        verify(productRepository, never()).save(any());
    }
    @Test
    void changeNameShouldUpdateAndSaveWhenNameIsDifferent() {
        UUID productId = UUID.randomUUID();
        Product product = mock(Product.class);
        when(productRepository.existsByNameValueAndRestaurantId("Frango Assado", restaurantId)).thenReturn(false);
        when(productRepository.findByIdAndRestaurantId(productId, restaurantId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        Product result = service.changeName(productId, "Frango Assado");
        assertSame(product, result);
        verify(product).changeName(new ProductName("Frango Assado"));
        verify(productRepository).save(product);
    }
    @Test
    void changeNameShouldThrowWhenNameAlreadyExistsInRestaurant() {
        UUID productId = UUID.randomUUID();
        when(productRepository.existsByNameValueAndRestaurantId("Frango Assado", restaurantId)).thenReturn(true);
        ProductAlreadyExistsException ex = assertThrows(
                ProductAlreadyExistsException.class,
                () -> service.changeName(productId, "Frango Assado")
        );
        assertEquals("Já existe um produto com esse nome neste restaurante.", ex.getMessage());
        verify(productRepository, never()).findByIdAndRestaurantId(any(), any());
    }
    @Test
    void changeNameShouldThrowWhenProductNotFound() {
        UUID productId = UUID.randomUUID();
        when(productRepository.existsByNameValueAndRestaurantId("Frango Assado", restaurantId)).thenReturn(false);
        when(productRepository.findByIdAndRestaurantId(productId, restaurantId)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> service.changeName(productId, "Frango Assado"));
        verify(productRepository, never()).save(any());
    }
    @Test
    void changeCategoryShouldUpdateAndSave() {
        UUID productId = UUID.randomUUID();
        Product product = mock(Product.class);
        when(productRepository.findByIdAndRestaurantId(productId, restaurantId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        Product result = service.changeCategory(productId, Category.SEAFOOD);
        assertSame(product, result);
        verify(product).changeCategory(Category.SEAFOOD);
        verify(productRepository).save(product);
    }
    @Test
    void deactivateShouldCallDeactivateAndSave() {
        UUID productId = UUID.randomUUID();
        Product product = mock(Product.class);
        when(productRepository.findByIdAndRestaurantId(productId, restaurantId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        Product result = service.deactivate(productId);
        assertSame(product, result);
        verify(product).deactivate();
        verify(productRepository).save(product);
    }
    @Test
    void deactivateShouldThrowWhenNotFound() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findByIdAndRestaurantId(productId, restaurantId)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> service.deactivate(productId));
        verify(productRepository, never()).save(any());
    }
    @Test
    void deactivateShouldPropagateWhenAlreadyInactive() {
        UUID productId = UUID.randomUUID();
        Product product = mock(Product.class);
        doThrow(new ProductActiveException("Produto já está inativo")).when(product).deactivate();
        when(productRepository.findByIdAndRestaurantId(productId, restaurantId)).thenReturn(Optional.of(product));
        ProductActiveException ex = assertThrows(ProductActiveException.class, () -> service.deactivate(productId));
        assertEquals("Produto já está inativo", ex.getMessage());
        verify(productRepository, never()).save(any());
    }
    @Test
    void reactivateShouldCallReactivateAndSave() {
        UUID productId = UUID.randomUUID();
        Product product = mock(Product.class);
        when(productRepository.findByIdAndRestaurantId(productId, restaurantId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        Product result = service.reactivate(productId);
        assertSame(product, result);
        verify(product).reactivate();
        verify(productRepository).save(product);
    }
    @Test
    void reactivateShouldThrowWhenNotFound() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findByIdAndRestaurantId(productId, restaurantId)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> service.reactivate(productId));
        verify(productRepository, never()).save(any());
    }
    @Test
    void reactivateShouldPropagateWhenAlreadyActive() {
        UUID productId = UUID.randomUUID();
        Product product = mock(Product.class);
        doThrow(new ProductActiveException("Produto já está ativo")).when(product).reactivate();
        when(productRepository.findByIdAndRestaurantId(productId, restaurantId)).thenReturn(Optional.of(product));
        ProductActiveException ex = assertThrows(ProductActiveException.class, () -> service.reactivate(productId));
        assertEquals("Produto já está ativo", ex.getMessage());
        verify(productRepository, never()).save(any());
    }
    @Test
    void getProductShouldReturnProductWhenFound() {
        UUID productId = UUID.randomUUID();
        Product product = mock(Product.class);
        when(productRepository.findByIdAndRestaurantId(productId, restaurantId)).thenReturn(Optional.of(product));
        assertSame(product, service.getProduct(productId));
    }
    @Test
    void getProductShouldThrowWhenNotFound() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findByIdAndRestaurantId(productId, restaurantId)).thenReturn(Optional.empty());
        ProductNotFoundException ex = assertThrows(ProductNotFoundException.class, () -> service.getProduct(productId));
        assertEquals("Produto não encontrado com id: " + productId, ex.getMessage());
    }
    @Test
    void getAllShouldReturnPageFromRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = mock(Product.class);
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
        when(productRepository.findAllProducts(null, null, restaurantId, pageable)).thenReturn(page);
        Page<Product> result = service.getAll(null, null, pageable);
        assertEquals(1, result.getContent().size());
        assertSame(product, result.getContent().get(0));
        verify(productRepository).findAllProducts(null, null, restaurantId, pageable);
    }
}

package br.com.matheusfragadev.lalouise.domain.restaurant.repository;
import br.com.matheusfragadev.lalouise.domain.restaurant.entity.Restaurant;
import br.com.matheusfragadev.lalouise.domain.restaurant.vo.Cnpj;
import br.com.matheusfragadev.lalouise.domain.restaurant.vo.RestaurantName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest removido no Spring Boot 4 — reescrever como teste de integração quando necessário
@Disabled("@DataJpaTest não disponível no Spring Boot 4")
class RestaurantRepositoryTest {
    @Autowired
    private RestaurantRepository restaurantRepository;
    private Object entityManager = null;
    private static final String CNPJ_LA_LOUISE  = "11222333000181";
    private static final String CNPJ_LE_GOURMET = "12345678000195";
    private static final String CNPJ_CAFE_PORTO = "11444777000161";
    @BeforeEach
    void setUp() {
        saveRestaurant("La Louise",    CNPJ_LA_LOUISE,  true);
        saveRestaurant("Le Gourmet",   CNPJ_LE_GOURMET, true);
        saveRestaurant("Cafe do Porto", CNPJ_CAFE_PORTO, false);
    }
    // ── findAllRestaurants — sem filtros ──────────────────────────────────────
    @Test
    void findAllRestaurantsShouldReturnAllWhenNoFiltersApplied() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> result = restaurantRepository.findAllRestaurants(null, null, pageable);
        assertEquals(3, result.getTotalElements());
    }
    @Test
    void findAllRestaurantsShouldRespectPageSizeAndReturnCorrectTotals() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Restaurant> result = restaurantRepository.findAllRestaurants(null, null, pageable);
        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
    }
    // ── findAllRestaurants — filtro por term (nome) ───────────────────────────
    @Test
    void findAllRestaurantsShouldFilterByTermMatchingName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> result = restaurantRepository.findAllRestaurants("La Louise", null, pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals("La Louise", result.getContent().get(0).getName().value());
    }
    @Test
    void findAllRestaurantsShouldFilterByTermPartialName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> result = restaurantRepository.findAllRestaurants("Gourmet", null, pageable);
        assertEquals(1, result.getTotalElements());
    }
    @Test
    void findAllRestaurantsShouldFilterByTermCaseInsensitiveName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> result = restaurantRepository.findAllRestaurants("LA LOUISE", null, pageable);
        assertEquals(1, result.getTotalElements());
    }
    // ── findAllRestaurants — filtro por term (cnpj) ───────────────────────────
    @Test
    void findAllRestaurantsShouldFilterByTermMatchingCnpj() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> result = restaurantRepository.findAllRestaurants(CNPJ_LA_LOUISE, null, pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals(CNPJ_LA_LOUISE, result.getContent().get(0).getCnpj().value());
    }
    @Test
    void findAllRestaurantsShouldFilterByTermPartialCnpj() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> result = restaurantRepository.findAllRestaurants("11222333", null, pageable);
        assertEquals(1, result.getTotalElements());
    }
    // ── findAllRestaurants — filtro por active ────────────────────────────────
    @Test
    void findAllRestaurantsShouldReturnOnlyActiveWhenActiveIsTrue() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> result = restaurantRepository.findAllRestaurants(null, true, pageable);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(Restaurant::isActive));
    }
    @Test
    void findAllRestaurantsShouldReturnOnlyInactiveWhenActiveIsFalse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> result = restaurantRepository.findAllRestaurants(null, false, pageable);
        assertEquals(1, result.getTotalElements());
        assertFalse(result.getContent().get(0).isActive());
        assertEquals("Cafe do Porto", result.getContent().get(0).getName().value());
    }
    // ── findAllRestaurants — combinacao de term + active ──────────────────────
    @Test
    void findAllRestaurantsShouldFilterByTermAndActiveTrue() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> result = restaurantRepository.findAllRestaurants("Le", true, pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals("Le Gourmet", result.getContent().get(0).getName().value());
    }
    @Test
    void findAllRestaurantsShouldFilterByTermAndActiveFalse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> result = restaurantRepository.findAllRestaurants("Porto", false, pageable);
        assertEquals(1, result.getTotalElements());
        assertFalse(result.getContent().get(0).isActive());
    }
    @Test
    void findAllRestaurantsShouldFilterByCnpjTermAndActiveTrue() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> result = restaurantRepository.findAllRestaurants(CNPJ_LA_LOUISE, true, pageable);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).isActive());
    }
    // ── findAllRestaurants — sem resultados ───────────────────────────────────
    @Test
    void findAllRestaurantsShouldReturnEmptyWhenTermDoesNotMatch() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> result = restaurantRepository.findAllRestaurants("xyz_inexistente", null, pageable);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }
    @Test
    void findAllRestaurantsShouldReturnEmptyWhenTermMatchesButActiveFilterExcludes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> result = restaurantRepository.findAllRestaurants("Porto", true, pageable);
        assertEquals(0, result.getTotalElements());
    }
    // ── helpers ───────────────────────────────────────────────────────────────
    private void saveRestaurant(String name, String cnpj, boolean active) {
        Restaurant restaurant = new Restaurant(new RestaurantName(name), new Cnpj(cnpj));
        if (!active) {
            restaurant.deactivate();
        }
    }
}

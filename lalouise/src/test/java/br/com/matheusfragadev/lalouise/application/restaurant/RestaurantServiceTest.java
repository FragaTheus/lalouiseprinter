package br.com.matheusfragadev.lalouise.application.restaurant;

import br.com.matheusfragadev.lalouise.domain.restaurant.entity.Restaurant;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.CnpjException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantActiveException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantNameException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantNotFoundException;
import br.com.matheusfragadev.lalouise.domain.restaurant.repository.RestaurantRepository;
import br.com.matheusfragadev.lalouise.domain.restaurant.vo.RestaurantName;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService service;

    // ── getRestaurant ─────────────────────────────────────────────────────────
    @Test
    void getRestaurantShouldReturnRestaurantWhenFound() {
        UUID id = UUID.randomUUID();
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));
        assertSame(restaurant, service.getRestaurant(id));
    }

    @Test
    void getRestaurantShouldThrowNotFoundWhenMissing() {
        UUID id = UUID.randomUUID();
        when(restaurantRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RestaurantNotFoundException.class, () -> service.getRestaurant(id));
    }

    // ── getAllRestaurants (pageable) ───────────────────────────────────────────
    @Test
    void getAllRestaurantsPageableShouldReturnPageFromRepository() {
        Restaurant r = mock(Restaurant.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> page = new PageImpl<>(List.of(r), pageable, 1);
        when(restaurantRepository.findAllRestaurants(null, null, pageable)).thenReturn(page);
        Page<Restaurant> result = service.getAllRestaurants(null, null, pageable);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertSame(r, result.getContent().get(0));
        verify(restaurantRepository).findAllRestaurants(null, null, pageable);
    }

    @Test
    void getAllRestaurantsPageableShouldReturnEmptyPageWhenNoneExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(restaurantRepository.findAllRestaurants(null, null, pageable)).thenReturn(emptyPage);
        Page<Restaurant> result = service.getAllRestaurants(null, null, pageable);
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(restaurantRepository).findAllRestaurants(null, null, pageable);
    }

    @Test
    void getAllRestaurantsPageableShouldPassTermAndActiveToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(restaurantRepository.findAllRestaurants("louise", true, pageable)).thenReturn(emptyPage);
        service.getAllRestaurants("louise", true, pageable);
        verify(restaurantRepository).findAllRestaurants("louise", true, pageable);
    }


    // ── create — caminho feliz ────────────────────────────────────────────────
    @Test
    void createShouldSaveAndReturnRestaurant() {
        when(restaurantRepository.existsByCnpjValue("11222333000181")).thenReturn(false);
        Restaurant result = service.create("La Louise", "11222333000181");
        assertNotNull(result);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void createShouldAcceptFormattedCnpj() {
        when(restaurantRepository.existsByCnpjValue("11222333000181")).thenReturn(false);
        assertDoesNotThrow(() -> service.create("La Louise", "11.222.333/0001-81"));
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    // ── create — erros de negócio ─────────────────────────────────────────────
    @Test
    void createShouldThrowWhenCnpjIsAlreadyRegistered() {
        when(restaurantRepository.existsByCnpjValue("11222333000181")).thenReturn(true);
        CnpjException ex = assertThrows(CnpjException.class,
                () -> service.create("La Louise", "11222333000181"));
        assertEquals("CNPJ já cadastrado", ex.getMessage());
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void createShouldThrowWhenCnpjIsInvalid() {
        assertThrows(CnpjException.class, () -> service.create("La Louise", "00000000000000"));
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void createShouldThrowWhenNameIsInvalid() {
        assertThrows(RestaurantNameException.class, () -> service.create("A", "11222333000181"));
        verify(restaurantRepository, never()).save(any());
    }

    // ── delete (soft delete) — caminho feliz ─────────────────────────────────
    @Test
    void deleteShouldDeactivateAndSave() {
        UUID id = UUID.randomUUID();
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));
        service.delete(id);
        verify(restaurant).deactivate();
        verify(restaurantRepository).save(restaurant);
    }

    // ── delete — erros ────────────────────────────────────────────────────────
    @Test
    void deleteShouldThrowWhenRestaurantNotFound() {
        UUID id = UUID.randomUUID();
        when(restaurantRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RestaurantNotFoundException.class, () -> service.delete(id));
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void deleteShouldThrowWhenRestaurantIsAlreadyInactive() {
        UUID id = UUID.randomUUID();
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));
        doThrow(new RestaurantActiveException("Restaurante já está inativo"))
                .when(restaurant).deactivate();
        assertThrows(RestaurantActiveException.class, () -> service.delete(id));
        verify(restaurantRepository, never()).save(any());
    }

    // ── changeName — caminho feliz ────────────────────────────────────────────
    @Test
    void changeNameShouldChangeNameAndSave() {
        UUID id = UUID.randomUUID();
        Restaurant restaurant = mock(Restaurant.class);
        RestaurantName currentName = mock(RestaurantName.class);
        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));
        when(restaurant.isActive()).thenReturn(true);
        when(restaurant.getName()).thenReturn(currentName);
        when(currentName.value()).thenReturn("Old Name");
        service.changeName(id, "New Name");
        verify(restaurant).changeName(new RestaurantName("New Name"));
        verify(restaurantRepository).save(restaurant);
    }

    // ── changeName — erros ────────────────────────────────────────────────────
    @Test
    void changeNameShouldThrowWhenRestaurantIsInactive() {
        UUID id = UUID.randomUUID();
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));
        when(restaurant.isActive()).thenReturn(false);
        RestaurantActiveException ex = assertThrows(
                RestaurantActiveException.class,
                () -> service.changeName(id, "New Name")
        );
        assertEquals("Não é possível alterar dados de um restaurante inativo.", ex.getMessage());
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void changeNameShouldThrowWhenRestaurantNotFound() {
        UUID id = UUID.randomUUID();
        when(restaurantRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(RestaurantNotFoundException.class, () -> service.changeName(id, "New Name"));
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void changeNameShouldThrowWhenNewNameIsIdenticalToCurrent() {
        UUID id = UUID.randomUUID();
        Restaurant restaurant = mock(Restaurant.class);
        RestaurantName currentName = mock(RestaurantName.class);
        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));
        when(restaurant.isActive()).thenReturn(true);
        when(restaurant.getName()).thenReturn(currentName);
        when(currentName.value()).thenReturn("Same Name");
        assertThrows(RestaurantNameException.class, () -> service.changeName(id, "Same Name"));
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void changeNameShouldThrowWhenNewNameIsInvalid() {
        UUID id = UUID.randomUUID();
        Restaurant restaurant = mock(Restaurant.class);
        when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));
        when(restaurant.isActive()).thenReturn(true);
        assertThrows(RestaurantNameException.class, () -> service.changeName(id, "A"));
        verify(restaurantRepository, never()).save(any());
    }
}

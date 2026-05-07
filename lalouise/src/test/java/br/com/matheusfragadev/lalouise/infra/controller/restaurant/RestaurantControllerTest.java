package br.com.matheusfragadev.lalouise.infra.controller.restaurant;

import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.domain.restaurant.entity.Restaurant;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.CnpjException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantActiveException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantNameException;
import br.com.matheusfragadev.lalouise.domain.restaurant.exception.RestaurantNotFoundException;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto.ChangeRestaurantNameRequest;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto.CreateRestaurantRequest;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto.RestaurantInfo;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.dto.RestaurantSummary;
import br.com.matheusfragadev.lalouise.infra.controller.restaurant.utils.mapper.RestaurantMapper;
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
class RestaurantControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private RestaurantController controller;

    // ── create ────────────────────────────────────────────────────────────────
    @Test
    void createShouldReturn201WithRestaurantId() {
        UUID id = UUID.randomUUID();
        Restaurant restaurant = mock(Restaurant.class);
        CreateRestaurantRequest request = new CreateRestaurantRequest("La Louise", "11222333000181");
        when(restaurant.getId()).thenReturn(id);
        when(restaurantService.create("La Louise", "11222333000181")).thenReturn(restaurant);
        ResponseEntity<String> response = controller.create(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(id.toString(), response.getBody());
        verify(restaurantService).create("La Louise", "11222333000181");
    }

    @Test
    void createShouldPropagateWhenCnpjIsAlreadyRegistered() {
        CreateRestaurantRequest request = new CreateRestaurantRequest("La Louise", "11222333000181");
        when(restaurantService.create(any(), any())).thenThrow(new CnpjException("CNPJ já cadastrado"));
        assertThrows(CnpjException.class, () -> controller.create(request));
        verify(restaurantService).create("La Louise", "11222333000181");
    }

    @Test
    void createShouldPropagateWhenCnpjIsInvalid() {
        CreateRestaurantRequest request = new CreateRestaurantRequest("La Louise", "00000000000000");
        when(restaurantService.create(any(), any())).thenThrow(new CnpjException("CNPJ inválido"));
        assertThrows(CnpjException.class, () -> controller.create(request));
    }

    @Test
    void createShouldPropagateWhenNameIsInvalid() {
        CreateRestaurantRequest request = new CreateRestaurantRequest("A", "11222333000181");
        when(restaurantService.create(any(), any())).thenThrow(new RestaurantNameException("Nome inválido"));
        assertThrows(RestaurantNameException.class, () -> controller.create(request));
    }

    // ── list ──────────────────────────────────────────────────────────────────
    @Test
    void listShouldReturn200WithMappedSummaries() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Restaurant r1 = mock(Restaurant.class);
        Restaurant r2 = mock(Restaurant.class);
        RestaurantSummary s1 = new RestaurantSummary(id1, "La Louise", true);
        RestaurantSummary s2 = new RestaurantSummary(id2, "Le Gourmet", true);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> restaurantPage = new PageImpl<>(List.of(r1, r2), pageable, 2);
        when(restaurantService.getAllRestaurants(null, null, pageable)).thenReturn(restaurantPage);
        try (MockedStatic<RestaurantMapper> mapper = mockStatic(RestaurantMapper.class)) {
            mapper.when(() -> RestaurantMapper.toRestaurantSummary(r1)).thenReturn(s1);
            mapper.when(() -> RestaurantMapper.toRestaurantSummary(r2)).thenReturn(s2);
            ResponseEntity<Page<RestaurantSummary>> response = controller.list(null, null, pageable);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().getContent().size());
            assertSame(s1, response.getBody().getContent().get(0));
            assertSame(s2, response.getBody().getContent().get(1));
        }
    }

    @Test
    void listShouldReturn200WithEmptyPageWhenNoRestaurantsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(restaurantService.getAllRestaurants(null, null, pageable)).thenReturn(emptyPage);
        ResponseEntity<Page<RestaurantSummary>> response = controller.list(null, null, pageable);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
    }

    @Test
    void listShouldFilterByTermAndActive() {
        UUID id = UUID.randomUUID();
        Restaurant r = mock(Restaurant.class);
        RestaurantSummary s = new RestaurantSummary(id, "La Louise", true);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> page = new PageImpl<>(List.of(r), pageable, 1);
        when(restaurantService.getAllRestaurants("La Louise", true, pageable)).thenReturn(page);
        try (MockedStatic<RestaurantMapper> mapper = mockStatic(RestaurantMapper.class)) {
            mapper.when(() -> RestaurantMapper.toRestaurantSummary(r)).thenReturn(s);
            ResponseEntity<Page<RestaurantSummary>> response = controller.list("La Louise", true, pageable);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().getContent().size());
            assertSame(s, response.getBody().getContent().get(0));
        }
    }

    // ── info ──────────────────────────────────────────────────────────────────
    @Test
    void infoShouldReturn200WithRestaurantInfo() {
        UUID id = UUID.randomUUID();
        Restaurant restaurant = mock(Restaurant.class);
        RestaurantInfo info = buildInfo(id);
        when(restaurantService.getRestaurant(id)).thenReturn(restaurant);
        try (MockedStatic<RestaurantMapper> mapper = mockStatic(RestaurantMapper.class)) {
            mapper.when(() -> RestaurantMapper.toRestaurantInfo(restaurant)).thenReturn(info);
            ResponseEntity<RestaurantInfo> response = controller.info(id);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(info, response.getBody());
            verify(restaurantService).getRestaurant(id);
        }
    }

    @Test
    void infoShouldPropagateWhenRestaurantNotFound() {
        UUID id = UUID.randomUUID();
        when(restaurantService.getRestaurant(id)).thenThrow(new RestaurantNotFoundException("Restaurante não encontrado"));
        assertThrows(RestaurantNotFoundException.class, () -> controller.info(id));
    }

    // ── update ────────────────────────────────────────────────────────────────
    @Test
    void updateShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        ChangeRestaurantNameRequest request = new ChangeRestaurantNameRequest("New Name");
        doNothing().when(restaurantService).changeName(id, "New Name");
        ResponseEntity<Void> response = controller.update(id, request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(restaurantService).changeName(id, "New Name");
    }

    @Test
    void updateShouldPropagateWhenRestaurantNotFound() {
        UUID id = UUID.randomUUID();
        ChangeRestaurantNameRequest request = new ChangeRestaurantNameRequest("New Name");
        doThrow(new RestaurantNotFoundException("Restaurante não encontrado"))
                .when(restaurantService).changeName(id, "New Name");
        assertThrows(RestaurantNotFoundException.class, () -> controller.update(id, request));
        verify(restaurantService).changeName(id, "New Name");
    }

    @Test
    void updateShouldPropagateWhenNameIsIdenticalToCurrent() {
        UUID id = UUID.randomUUID();
        ChangeRestaurantNameRequest request = new ChangeRestaurantNameRequest("Same Name");
        doThrow(new RestaurantNameException("Novo nome deve ser diferente do atual"))
                .when(restaurantService).changeName(id, "Same Name");
        assertThrows(RestaurantNameException.class, () -> controller.update(id, request));
    }

    @Test
    void updateShouldPropagateWhenNameIsInvalid() {
        UUID id = UUID.randomUUID();
        ChangeRestaurantNameRequest request = new ChangeRestaurantNameRequest("A");
        doThrow(new RestaurantNameException("Nome inválido"))
                .when(restaurantService).changeName(id, "A");
        assertThrows(RestaurantNameException.class, () -> controller.update(id, request));
    }

    // ── delete ────────────────────────────────────────────────────────────────
    @Test
    void deleteShouldReturn204NoContent() {
        UUID id = UUID.randomUUID();
        doNothing().when(restaurantService).delete(id);
        ResponseEntity<Void> response = controller.delete(id);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(restaurantService).delete(id);
    }

    @Test
    void deleteShouldPropagateWhenRestaurantNotFound() {
        UUID id = UUID.randomUUID();
        doThrow(new RestaurantNotFoundException("Restaurante não encontrado"))
                .when(restaurantService).delete(id);
        assertThrows(RestaurantNotFoundException.class, () -> controller.delete(id));
    }

    @Test
    void deleteShouldPropagateWhenRestaurantAlreadyInactive() {
        UUID id = UUID.randomUUID();
        doThrow(new RestaurantActiveException("Restaurante já está inativo"))
                .when(restaurantService).delete(id);
        assertThrows(RestaurantActiveException.class, () -> controller.delete(id));
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private RestaurantInfo buildInfo(UUID id) {
        return RestaurantInfo.builder()
                .id(id)
                .name("La Louise")
                .cnpj("11222333000181")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}


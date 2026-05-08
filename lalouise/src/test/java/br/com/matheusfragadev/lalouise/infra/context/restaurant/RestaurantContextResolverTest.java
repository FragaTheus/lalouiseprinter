package br.com.matheusfragadev.lalouise.infra.context.restaurant;

import br.com.matheusfragadev.lalouise.domain.user.admin.entity.Admin;
import br.com.matheusfragadev.lalouise.domain.user.credentials.entity.Credentials;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Manager;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantContextResolverTest {

    @InjectMocks
    private RestaurantContextResolver resolver;

    private static final UUID RESTAURANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @BeforeEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
        RestaurantContext.clear();
    }

    private void authenticateWith(Credentials credentials) {
        var userDetails = new UserDetailsImpl(credentials);
        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private HttpServletRequest requestFor(String uri) {
        var req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn(uri);
        return req;
    }

    @Test
    void shouldResolveRestaurantIdFromStaffCredentials() {
        Manager manager = mock(Manager.class);
        when(manager.getRestaurantId()).thenReturn(RESTAURANT_ID);
        authenticateWith(manager);

        var req = requestFor("/api/v1/restaurants/" + RESTAURANT_ID + "/managers");
        var resolved = resolver.resolve(req);

        assertEquals(RESTAURANT_ID, resolved);
    }

    @Test
    void shouldThrowWhenStaffAccessesDifferentRestaurant() {
        UUID anotherRestaurant = UUID.randomUUID();
        Manager manager = mock(Manager.class);
        when(manager.getRestaurantId()).thenReturn(RESTAURANT_ID);
        authenticateWith(manager);

        var req = requestFor("/api/v1/restaurants/" + anotherRestaurant + "/managers");

        assertThrows(AccessDeniedException.class, () -> resolver.resolve(req));
    }

    @Test
    void shouldResolveRestaurantIdFromUrlForAdmin() {
        Admin admin = mock(Admin.class);
        authenticateWith(admin);

        var req = requestFor("/api/v1/restaurants/" + RESTAURANT_ID + "/managers");
        var resolved = resolver.resolve(req);

        assertEquals(RESTAURANT_ID, resolved);
    }

    @Test
    void shouldReturnNullWhenNoAuthentication() {
        var req = mock(HttpServletRequest.class);
        var resolved = resolver.resolve(req);

        assertNull(resolved);
    }
}


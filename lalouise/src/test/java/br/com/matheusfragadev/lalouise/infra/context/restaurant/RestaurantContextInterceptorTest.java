package br.com.matheusfragadev.lalouise.infra.context.restaurant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantContextInterceptorTest {

    @Mock
    private RestaurantContextResolver resolver;

    @InjectMocks
    private RestaurantContextInterceptor interceptor;

    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final Object handler = new Object();

    @AfterEach
    void cleanup() {
        RestaurantContext.clear();
    }

    @Test
    void preHandleShouldSetContextWhenRestaurantIdResolved() {
        var id = UUID.randomUUID();
        when(request.getRequestURI()).thenReturn("/api/v1/restaurants/" + id + "/managers");
        when(resolver.resolve(request)).thenReturn(id);

        boolean proceed = interceptor.preHandle(request, response, handler);

        assertTrue(proceed);
        assertEquals(id, RestaurantContext.get());
    }

    @Test
    void preHandleShouldNotSetContextWhenResolverReturnsNull() {
        when(resolver.resolve(request)).thenReturn(null);

        boolean proceed = interceptor.preHandle(request, response, handler);

        assertTrue(proceed);
        assertNull(RestaurantContext.get());
    }

    @Test
    void afterCompletionShouldAlwaysClearContext() {
        RestaurantContext.set(UUID.randomUUID());

        interceptor.afterCompletion(request, response, handler, null);

        assertNull(RestaurantContext.get());
    }

    @Test
    void afterCompletionShouldClearEvenOnException() {
        RestaurantContext.set(UUID.randomUUID());

        interceptor.afterCompletion(request, response, handler, new RuntimeException("error"));

        assertNull(RestaurantContext.get());
    }
}


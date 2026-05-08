package br.com.matheusfragadev.lalouise.infra.context.restaurant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantContextInterceptor implements HandlerInterceptor {

    private final RestaurantContextResolver resolver;

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {
        var restaurantId = resolver.resolve(request);
        if (restaurantId != null) {
            RestaurantContext.set(restaurantId);
            log.debug("RestaurantContext set to {} for request {}", restaurantId, request.getRequestURI());
        }
        return true;
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            Exception ex
    ) {
        RestaurantContext.clear();
        log.debug("RestaurantContext cleared after request {}", request.getRequestURI());
    }
}


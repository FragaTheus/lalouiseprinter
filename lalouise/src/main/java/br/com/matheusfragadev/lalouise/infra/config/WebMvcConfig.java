package br.com.matheusfragadev.lalouise.infra.config;

import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContextInterceptor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RestaurantContextInterceptor restaurantContextInterceptor;

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(restaurantContextInterceptor)
                .addPathPatterns("/api/v1/restaurants/**");
    }
}


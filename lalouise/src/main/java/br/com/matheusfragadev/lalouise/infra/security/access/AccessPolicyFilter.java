package br.com.matheusfragadev.lalouise.infra.security.access;

import br.com.matheusfragadev.lalouise.application.accesspolicy.AccessPolicyService;
import br.com.matheusfragadev.lalouise.domain.user.credentials.enums.Role;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import br.com.matheusfragadev.lalouise.infra.context.sector.SectorContext;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import br.com.matheusfragadev.lalouise.infra.security.jwt.JwtFilter;
import br.com.matheusfragadev.lalouise.infra.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccessPolicyFilter extends OncePerRequestFilter {

    private final AccessPolicyService accessPolicyService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    )
            throws ServletException, IOException {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (
                authentication != null &&
                        authentication.isAuthenticated() &&
                        authentication.getPrincipal() instanceof UserDetailsImpl userDetails
        ) {
            var restaurantId = RestaurantContext.get();
            var sectorId = SectorContext.get();

            if (restaurantId != null) {
                var claims = jwtService.extractClaims(JwtFilter.resolveToken(request));
                var userRestaurantId = claims.get("restaurantId", String.class);
                var role = claims.get("role", String.class);
                if (sectorId != null) {
                    var userSectorId = claims.get("sectorId", String.class);
                    accessPolicyService.isTheSameSector(UUID.fromString(userRestaurantId), UUID.fromString(userSectorId), Role.valueOf(role));
                    filterChain.doFilter(request, response);
                    return;
                }
                accessPolicyService.isTheSameRestaurant(UUID.fromString(userRestaurantId), Role.valueOf(role));
            }
        }
        filterChain.doFilter(request, response);
    }
}
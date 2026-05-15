package br.com.matheusfragadev.lalouise.infra.context.restaurant;

import br.com.matheusfragadev.lalouise.domain.user.staff.entity.BaseStaff;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class RestaurantContextResolver {

    private static final Pattern RESTAURANT_ID_PATTERN =
            Pattern.compile("/api/v1/restaurants/([0-9a-fA-F\\-]{36})");

    public UUID resolve(HttpServletRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            log.warn("No authenticated user found when resolving restaurant context");
            return null;
        }

        var credentials = userDetails.getCredentials();

        if (credentials instanceof BaseStaff baseStaff) {
            var staffRestaurantId = baseStaff.getRestaurantId();
            var urlRestaurantId = extractFromUrl(request.getRequestURI());

            if (urlRestaurantId != null && !urlRestaurantId.equals(staffRestaurantId)) {
                log.warn("Staff {} attempted to access restaurant {} but belongs to {}",
                        userDetails.getId(), urlRestaurantId, staffRestaurantId);
                throw new AccessDeniedException("Acesso negado: você não pertence a este restaurante");
            }

            log.debug("Resolved restaurant context from staff credentials: {}", staffRestaurantId);
            return staffRestaurantId;
        }

        var restaurantId = extractFromUrl(request.getRequestURI());
        log.debug("Resolved restaurant context from URL for admin: {}", restaurantId);
        return restaurantId;
    }

    private UUID extractFromUrl(String uri) {
        Matcher matcher = RESTAURANT_ID_PATTERN.matcher(uri);
        if (matcher.find()) {
            return UUID.fromString(matcher.group(1));
        }
        return null;
    }
}


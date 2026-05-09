package br.com.matheusfragadev.lalouise.infra.context.sector;

import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Staff;
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
public class SectorContextResolver {

    private static final Pattern SECTOR_ID_PATTERN =
            Pattern.compile("/api/v1/restaurants/[0-9a-fA-F\\-]{36}/sectors/([0-9a-fA-F\\-]{36})");

    private static final Pattern RESTAURANT_ID_PATTERN =
            Pattern.compile("/api/v1/restaurants/([0-9a-fA-F\\-]{36})/sectors/");

    public UUID resolve(HttpServletRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            log.warn("No authenticated user found when resolving sector context");
            return null;
        }

        var credentials = userDetails.getCredentials();

        if (credentials instanceof Staff staff) {
            var urlRestaurantId = extractRestaurantFromUrl(request.getRequestURI());

            if (urlRestaurantId != null && !urlRestaurantId.equals(staff.getRestaurantId())) {
                log.warn("Staff {} attempted to access sectors of restaurant {} but belongs to {}",
                        userDetails.getId(), urlRestaurantId, staff.getRestaurantId());
                throw new AccessDeniedException("Acesso negado: você não pertence a este restaurante");
            }
        }

        var sectorId = extractSectorFromUrl(request.getRequestURI());
        log.debug("Resolved sector context from URL: {}", sectorId);
        return sectorId;
    }

    private UUID extractSectorFromUrl(String uri) {
        Matcher matcher = SECTOR_ID_PATTERN.matcher(uri);
        if (matcher.find()) {
            return UUID.fromString(matcher.group(1));
        }
        return null;
    }

    private UUID extractRestaurantFromUrl(String uri) {
        Matcher matcher = RESTAURANT_ID_PATTERN.matcher(uri);
        if (matcher.find()) {
            return UUID.fromString(matcher.group(1));
        }
        return null;
    }
}



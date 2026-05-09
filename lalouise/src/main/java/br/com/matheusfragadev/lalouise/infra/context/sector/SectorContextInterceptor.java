package br.com.matheusfragadev.lalouise.infra.context.sector;

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
public class SectorContextInterceptor implements HandlerInterceptor {

    private final SectorContextResolver resolver;

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {
        var sectorId = resolver.resolve(request);
        if (sectorId != null) {
            SectorContext.set(sectorId);
            log.debug("SectorContext set to {} for request {}", sectorId, request.getRequestURI());
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
        SectorContext.clear();
        log.debug("SectorContext cleared after request {}", request.getRequestURI());
    }
}


package br.com.matheusfragadev.lalouise.infra.security.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String RATE_LIMIT_KEY = "rate_limit";

    public void validateRateLimit(String ip, String endpoint, int maxRequests){
        var key = RATE_LIMIT_KEY + ip + ":" + endpoint;

        try {
            Long requestCount = redisTemplate.opsForValue().increment(key);
            if (requestCount == 1) {
                redisTemplate.expire(key, 1, TimeUnit.MINUTES);
            }

            if (requestCount > maxRequests) {
                log.warn("Rate limit excedido para o IP: {} no seguinte endpoint: {}", ip, endpoint);
                throw new RateLimitException("Muitas requisições. Por favor, tente novamente mais tarde.");
            }
        }catch (RateLimitException e){
            throw e;
        }catch (Exception e){
            log.error("Erro ao verificar o rate limit para o IP: {} no endpoint: {}. Erro: {}", ip, endpoint, e.getMessage());
        }
    }

}

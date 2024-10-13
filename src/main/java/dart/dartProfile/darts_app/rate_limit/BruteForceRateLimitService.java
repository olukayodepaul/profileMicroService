package dart.dartProfile.darts_app.rate_limit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BruteForceRateLimitService {

    private static final Logger logger = LoggerFactory.getLogger(BruteForceRateLimitService.class);
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public BruteForceRateLimitService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public boolean isRateLimited(String uuid) {
        try {
            String key = uuid;
            String lastUpdateKey = key + ":lastUpdate";
            String leakRateKey = key + ":leakRate";

            // Initialize leak rate and last update if not existing
            if (Boolean.FALSE.equals(redisTemplate.hasKey(leakRateKey))) {
                redisTemplate.opsForValue().set(leakRateKey, "1"); // 1 request/second
                redisTemplate.expire(leakRateKey, 2, TimeUnit.MINUTES);
            }

            if (Boolean.FALSE.equals(redisTemplate.hasKey(lastUpdateKey))) {
                redisTemplate.opsForValue().set(lastUpdateKey, String.valueOf(System.currentTimeMillis() / 1000));
                redisTemplate.expire(lastUpdateKey, 2, TimeUnit.MINUTES);
            }

            String requestsStr = redisTemplate.opsForValue().get(key);
            Long requests = requestsStr != null ? Long.parseLong(requestsStr) : 0L;

            Long leakRate = Long.parseLong(redisTemplate.opsForValue().get(leakRateKey));
            Long lastUpdate = Long.parseLong(redisTemplate.opsForValue().get(lastUpdateKey));

            Long now = System.currentTimeMillis() / 1000;
            Long elapsed = now - lastUpdate;
            requests = Math.max(0, requests - elapsed * leakRate);

            // Check if rate limit has been exceeded
            if (requests >= 3) { // 3 requests/minute
                return true;
            }

            // Increment request count
            requests++;
            redisTemplate.opsForValue().set(key, String.valueOf(requests));
            redisTemplate.opsForValue().set(lastUpdateKey, String.valueOf(now));
            redisTemplate.expire(key, 2, TimeUnit.MINUTES);
            redisTemplate.expire(lastUpdateKey, 2, TimeUnit.MINUTES);

            return false;
        } catch (Exception e) {
            logger.error("RateLimitService::isRateLimited Failed to connect to Redis server. Error: {}", e.getMessage());
            return false;
        }
    }

    // Method to manually reset the rate limit for a user (if needed)
    public void resetRateLimit(String uuid) {
        String key = uuid;
        String lastUpdateKey = key + ":lastUpdate";
        String leakRateKey = key + ":leakRate";
        redisTemplate.delete(key);
        redisTemplate.delete(lastUpdateKey);
        redisTemplate.delete(leakRateKey);
    }
}

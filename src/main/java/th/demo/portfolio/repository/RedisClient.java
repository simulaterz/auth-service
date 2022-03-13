package th.demo.portfolio.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class RedisClient {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisClient(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public <T> void setHash(String h, Map<String, T> value) {
        HashOperations<String, String, T> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(h, value);
    }

    public void setValue(String key, String value) {
        log.debug("Redis client : Set Value from key = {}, value = {}", key, value);
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValue(String key, String value, Long expireTime) {
        log.debug("Redis client : Set Value from key = {}, value = {}", key, value);
        Duration duration = Duration.ofSeconds(expireTime);
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public void setObject(String key, Object value) throws JsonProcessingException {
        log.info("Redis client : Set Object from key = {}, value = {}", key, value);
        String ObjectString = objectMapper.writeValueAsString(value);
        redisTemplate.opsForValue().set(key, ObjectString);
    }

    public void setObject(String key, Object value, Long expireTime) throws JsonProcessingException {
        log.debug("Redis client : Set Object from key = {}, value = {}", key, value);
        String objectString = objectMapper.writeValueAsString(value);
        Duration duration = Duration.ofSeconds(expireTime);
        redisTemplate.opsForValue().set(key, objectString, duration);
    }

    public <T> T getHash(String h, String hk, Class c) {
        return (T) c.cast(this.redisTemplate.opsForHash().get(h, hk));
    }

    public <T> List<T> getAllHash(String h) {
        return (List<T>) this.redisTemplate.opsForHash().values(h);
    }

    public Map<Object, Object> getAllHashToMap(String h) {
        return this.redisTemplate.opsForHash().entries(h);
    }

    public String getValueByKey(String key) {
        log.debug("Redis client : Get Value from key = {}", key);
        String value = redisTemplate.opsForValue().get(key);
        if (value != null && !(value.isEmpty())) {
            return value;
        }
        return null;
    }

    public <T> T getObjectByKey(String key, Class<T> tClass) throws JsonProcessingException {
        log.debug("Redis client : Get Object from key = {}", key);
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? objectMapper.readValue(value, tClass) : null;
    }

    public void del(String key) {
        log.debug("Redis client : Delete key = {}", key);
        redisTemplate.delete(key);
    }

    public void setExpire(String key, Duration ttl) {
        log.debug("Redis client : setExpire key = {}", key);
        redisTemplate.expire(key, ttl);
    }

    public void increase(String key) {
        log.debug("Redis client : increase key = {}", key);
        redisTemplate.opsForValue().increment(key);
    }
}

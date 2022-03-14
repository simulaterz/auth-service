package th.demo.portfolio.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Repository
public class RedisClient {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisClient(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public <T> void setHash(String h, Map<String, T> value) {
        this.redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

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
        log.debug("Redis client : Set Object from key = {}, value = {}", key, value);
        var objectString = objectMapper.writeValueAsString(value);
        redisTemplate.opsForValue().set(key, objectString);
    }

    public void setObject(String key, Object value, Long expireTime) throws JsonProcessingException {
        log.debug("Redis client : Set Object from key = {}, value = {}", key, value);
        var objectString = objectMapper.writeValueAsString(value);
        var duration = Duration.ofSeconds(expireTime);
        redisTemplate.opsForValue().set(key, objectString, duration);
    }

    public <T> T getHash(String h, String hk, Class<T> c) {
        return c.cast(this.redisTemplate.opsForHash().get(h, hk));
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
        var value = redisTemplate.opsForValue().get(key);
        return value != null ? objectMapper.readValue(value, tClass) : null;
    }

    public void del(String key) {
        log.debug("Redis client : Delete key = {}", key);
        redisTemplate.delete(key);
    }

    public void delRootKey(String keyInput) {
        var keys = getKeys(keyInput + "*");
        for (String key : keys) {
            log.debug("Redis Key {}", key);
            redisTemplate.delete(key);
        }
    }

    private Set<String> getKeys(String h) {
        return this.redisTemplate.keys(h);
    }

    public void setExpire(String key, Duration ttl) {
        log.debug("Redis client : setExpire key = {}", key);
        redisTemplate.expire(key, ttl);
    }

    public void increase(String key) {
        log.debug("Redis client : increase key = {}", key);
        redisTemplate.opsForValue().increment(key);
    }

    public Boolean hasKey(String h) {
        return this.redisTemplate.hasKey(h);
    }
}

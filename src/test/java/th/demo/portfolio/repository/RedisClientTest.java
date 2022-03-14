package th.demo.portfolio.repository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import th.demo.portfolio.TestRedisConfiguration;
import th.demo.portfolio.model.BaseUserModel;
import th.demo.portfolio.model.redis.AccessTokenRedis;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest(classes = TestRedisConfiguration.class)
@DisplayName("Test -> RedisClient")
class RedisClientTest {

    @Autowired
    private RedisClient client;

    @AfterEach
    public void afterEach() {
        // delete all keys
        client.delRootKey("");
    }

    private final String key = "redis-key";
    private final String value = "redis-value";
    private final String hashKey = "hash-key";
    private final BaseUserModel user = BaseUserModel.builder().firstName("mock").build();
    private final AccessTokenRedis hashValue = AccessTokenRedis.builder().baseUserModel(user).build();

    @Test
    @DisplayName("setHash, expected no error")
    void setHash() {
        var mapObject = Map.of(hashKey, hashValue);
        assertDoesNotThrow(() -> client.setHash(key, mapObject));
    }

    @Test
    @DisplayName("setValue, expected no error")
    void setValue() {
        assertDoesNotThrow(() -> client.setValue(key, value));
    }

    @Test
    @DisplayName("setValueWithExpire, expected no error")
    void setValueWithExpire() {
        assertDoesNotThrow(() -> client.setValue(key, value, 1L));
    }

    @Test
    @DisplayName("setObject, expected no error")
    void setObject() {
        assertDoesNotThrow(() -> client.setObject(key, user));
    }

    @Test
    @DisplayName("setObjectWithExpire, expected no error")
    void setObjectWithExpire() {
        assertDoesNotThrow(() -> client.setObject(key, user, 1L));
    }

    @Test
    @DisplayName("getHash, expected can deserialized data")
    void getHash() {
        setHash();

        var response = client.getHash(key, hashKey, AccessTokenRedis.class);
        assertEquals(hashValue, response);
        assertEquals("mock", response.getBaseUserModel().getFirstName());
    }

    @Test
    @DisplayName("getAllHash, expected can deserialized data")
    void getAllHash() {
        setHash();

        var responseList = client.<AccessTokenRedis>getAllHash(key);
        assertTrue(responseList.contains(hashValue));
    }

    @Test
    @DisplayName("getAllHashToMap, expected can deserialized data")
    void getAllHashToMap() {
        setHash();

        var responseMap = client.getAllHashToMap(key);
        assertEquals(hashValue, responseMap.get(hashKey));
        assertEquals("mock", ((AccessTokenRedis) responseMap.get(hashKey)).getBaseUserModel().getFirstName());
    }

    @Test
    @SneakyThrows
    @DisplayName("getValueByKey with expire and no expire time, expected success")
    void getValueByKey() {
        setValue();

        var response = client.getValueByKey(key);
        assertEquals(value, response);

        setValueWithExpire();
        SECONDS.sleep(2);

        var expired = client.getValueByKey(key);
        assertNull(expired);
    }

    @Test
    @SneakyThrows
    @DisplayName("getObjectByKey with expire and no expire time, expected success")
    void getObjectByKey() {
        setObject();

        var response = client.getObjectByKey(key, BaseUserModel.class);
        assertEquals(user, response);

        setValueWithExpire();
        SECONDS.sleep(2);

        var expired = client.getObjectByKey(key, BaseUserModel.class);
        assertNull(expired);
    }

    @Test
    @DisplayName("del, expected success")
    void del() {
        setValue();

        assertTrue(client.hasKey(key));
        client.del(key);
        assertFalse(client.hasKey(key));

        setObject();

        assertTrue(client.hasKey(key));
        client.del(key);
        assertFalse(client.hasKey(key));
    }

    @Test
    @SneakyThrows
    @DisplayName("setExpire, expected success")
    void setExpire() {
        setValue();

        assertTrue(client.hasKey(key));
        client.setExpire(key, Duration.ofSeconds(1L));

        SECONDS.sleep(2);
        assertFalse(client.hasKey(key));
    }

    @Test
    @DisplayName("increase, expected success")
    void increase() {
        client.increase("inc");
        assertEquals(Integer.toString(1), client.getValueByKey("inc"));

        client.increase("inc");
        assertEquals(Integer.toString(2), client.getValueByKey("inc"));
    }
}
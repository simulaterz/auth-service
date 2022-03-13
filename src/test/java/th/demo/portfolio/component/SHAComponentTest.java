package th.demo.portfolio.component;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("Test -> SHAComponent")
class SHAComponentTest {

    @InjectMocks
    private SHAComponent component;

    @Test
    @SneakyThrows
    @DisplayName("generate hash multiple time, expected same result")
    void generateAndValidateHash() {

        var hash1 = component.toSHA256String("SIMPLE");
        var hash2 = component.toSHA256String("SIMPLE");
        var hash3 = component.toSHA256String("SIMPLE");

        Assertions.assertEquals(hash2, hash1);
        Assertions.assertEquals(hash3, hash1);
    }
}
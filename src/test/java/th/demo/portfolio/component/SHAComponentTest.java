package th.demo.portfolio.component;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        assertEquals(hash2, hash1);
        assertEquals(hash3, hash1);
    }
}
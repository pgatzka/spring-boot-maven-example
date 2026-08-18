package io.github.pgatzka.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class ApplicationTest {

    @Test
    void contextLoads() {
        assertTrue(true);
    }
}

package io.github.pgatzka.example.domain.repository;

import io.github.pgatzka.example.TestcontainersConfiguration;
import io.github.pgatzka.example.configuration.JpaConfiguration;
import io.github.pgatzka.example.domain.entity.Greeting;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestcontainersConfiguration.class, JpaConfiguration.class})
class GreetingRepositoryTest {

    private static final String AUTHOR = "Raymond";

    private static final String MESSAGE = "If horses were wishes, beggars would ride.";

    private static final String SUBJECT = "Donald";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GreetingRepository repository;

    private Greeting persistGreeting() {
        return entityManager.persistFlushFind(new Greeting(AUTHOR, MESSAGE, SUBJECT));
    }

    @Nested
    @DisplayName("findByUuid(UUID)")
    class FindByUuid {

        @Test
        void returnsGreetingWithMatchingUuid() {
            Greeting greeting = persistGreeting();

            assertThat(repository.findByUuid(greeting.getUuid())).contains(greeting);
        }

        @Test
        void returnsEmptyForUnknownUuid() {
            persistGreeting();

            assertThat(repository.findByUuid(UUID.randomUUID())).isEmpty();
        }

    }

    @Nested
    @DisplayName("existsByUuid(UUID)")
    class ExistsByUuid {

        @Test
        void returnsTrueForKnownUuid() {
            Greeting greeting = persistGreeting();

            assertThat(repository.existsByUuid(greeting.getUuid())).isTrue();
        }

        @Test
        void returnsFalseForUnknownUuid() {
            persistGreeting();

            assertThat(repository.existsByUuid(UUID.randomUUID())).isFalse();
        }

    }

    @Nested
    @DisplayName("deleteByUuid(UUID)")
    class DeleteByUuid {

        @Test
        void removesGreetingWithMatchingUuid() {
            Greeting greeting = persistGreeting();

            repository.deleteByUuid(greeting.getUuid());
            entityManager.flush();

            assertThat(repository.existsByUuid(greeting.getUuid())).isFalse();
        }

        @Test
        void keepsGreetingsWithOtherUuids() {
            Greeting greeting = persistGreeting();

            repository.deleteByUuid(UUID.randomUUID());
            entityManager.flush();

            assertThat(repository.existsByUuid(greeting.getUuid())).isTrue();
        }

    }

}

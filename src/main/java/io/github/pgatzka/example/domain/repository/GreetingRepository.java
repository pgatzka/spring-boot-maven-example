package io.github.pgatzka.example.domain.repository;

import io.github.pgatzka.example.domain.entity.Greeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GreetingRepository extends JpaRepository<Greeting, Long> {

    boolean existsByUuid(UUID uuid);

    Optional<Greeting> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

}

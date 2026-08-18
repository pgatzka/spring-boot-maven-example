package io.github.pgatzka.example.domain.repository;

import io.github.pgatzka.example.domain.entity.Greeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GreetingRepository extends JpaRepository<Greeting, Long> {

}

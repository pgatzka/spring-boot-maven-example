package io.github.pgatzka.example;


import io.github.pgatzka.example.domain.entity.Greeting;
import io.github.pgatzka.example.domain.repository.GreetingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@SpringBootApplication
public class Application {

    private final GreetingRepository greetingRepository;

    public Application(GreetingRepository greetingRepository) {
        this.greetingRepository = greetingRepository;
    }

    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent(ApplicationReadyEvent event){
        log.info("Inserting greeting");

        Greeting greeting = new Greeting("Philipp", "Was machen sachen?", "Marco");
        greetingRepository.save(greeting);
    }

}

package io.github.pgatzka.example.exception;

import io.github.pgatzka.example.rest.exception.NotFoundException;

import java.util.UUID;

public class GreetingNotFoundException extends NotFoundException {

    public GreetingNotFoundException(UUID uuid) {
        super("Greeting '" + uuid + "' not found");
    }
}

package io.github.pgatzka.example.rest.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record GreetRequest(@NotBlank @Length(max = 30) String author, @Length(max = 200) String message,
                           @NotBlank @Length(max = 30) String subject) {
}

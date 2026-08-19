
package io.github.pgatzka.example.rest.response.response;

import java.util.UUID;

public record GreetingResponse(UUID uuid, String author, String message, String subject) {
}


package io.github.pgatzka.example.integration;

import io.github.pgatzka.example.TestcontainersConfiguration;
import io.github.pgatzka.example.rest.request.GreetingCreateRequest;
import io.github.pgatzka.example.rest.request.UpdateGreetingRequest;
import io.github.pgatzka.example.rest.response.response.GreetingResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingIntegrationTest {

    private static final String BASE_PATH = "/api/v1/greeting";

    private static final String AUTHOR = "Raymond";

    private static final String MESSAGE = "If horses were wishes, beggars would ride.";

    private static final String SUBJECT = "Donald";

    @Autowired
    private RestTestClient client;

    private GreetingResponse createGreeting() {
        return client.post().uri(BASE_PATH + "/greet").contentType(MediaType.APPLICATION_JSON)
                        .body(new GreetingCreateRequest(AUTHOR, MESSAGE, SUBJECT)).exchange().expectStatus().isCreated()
                        .expectBody(GreetingResponse.class).returnResult().getResponseBody();
    }

    @Test
    void createdGreetingIsRetrievableAtItsLocation() {
        URI location = client.post().uri(BASE_PATH + "/greet").contentType(MediaType.APPLICATION_JSON)
                        .body(new GreetingCreateRequest(AUTHOR, MESSAGE, SUBJECT)).exchange().expectStatus().isCreated()
                        .expectBody(GreetingResponse.class).returnResult().getResponseHeaders().getLocation();

        assertThat(location).isNotNull();

        client.get().uri(location).exchange().expectStatus().isOk().expectHeader()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON).expectBody().jsonPath("$.author")
                        .isEqualTo(AUTHOR).jsonPath("$.message").isEqualTo(MESSAGE).jsonPath("$.subject")
                        .isEqualTo(SUBJECT);
    }

    @Test
    void createdGreetingIsListedFirstAsTheMostRecentOne() {
        GreetingResponse created = createGreeting();

        client.get().uri(BASE_PATH + "?size=1").exchange().expectStatus().isOk().expectBody().jsonPath("$.page.size")
                        .isEqualTo(1).jsonPath("$.content[0].uuid").isEqualTo(created.uuid().toString());
    }

    @Test
    void updatedGreetingIsPersisted() {
        GreetingResponse created = createGreeting();

        client.put().uri(BASE_PATH + "/{uuid}", created.uuid()).contentType(MediaType.APPLICATION_JSON)
                        .body(new UpdateGreetingRequest("Donald", "Wishes granted.", "Raymond")).exchange()
                        .expectStatus().isOk().expectBody().jsonPath("$.uuid").isEqualTo(created.uuid().toString())
                        .jsonPath("$.author").isEqualTo("Donald");

        client.get().uri(BASE_PATH + "/{uuid}", created.uuid()).exchange().expectStatus().isOk().expectBody()
                        .jsonPath("$.author").isEqualTo("Donald").jsonPath("$.message").isEqualTo("Wishes granted.")
                        .jsonPath("$.subject").isEqualTo("Raymond");
    }

    @Test
    void deletedGreetingIsNoLongerRetrievable() {
        GreetingResponse created = createGreeting();

        client.delete().uri(BASE_PATH + "/{uuid}", created.uuid()).exchange().expectStatus().isNoContent().expectBody()
                        .isEmpty();

        client.get().uri(BASE_PATH + "/{uuid}", created.uuid()).exchange().expectStatus().isNotFound();
    }

    @Test
    void unknownGreetingIsReportedAsNotFound() {
        UUID unknown = UUID.randomUUID();

        client.get().uri(BASE_PATH + "/{uuid}", unknown).exchange().expectStatus().isNotFound().expectBody()
                        .jsonPath("$.status").isEqualTo(404).jsonPath("$.detail")
                        .isEqualTo("Greeting '" + unknown + "' not found");
    }

    @Test
    void invalidGreetingIsRejectedAndNotPersisted() {
        client.post().uri(BASE_PATH + "/greet").contentType(MediaType.APPLICATION_JSON)
                        .body(new GreetingCreateRequest(" ", MESSAGE, "A".repeat(50))).exchange().expectStatus()
                        .isBadRequest().expectBody().jsonPath("$.status").isEqualTo(400).jsonPath("$.errors.author")
                        .exists().jsonPath("$.errors.subject").exists();
    }

}

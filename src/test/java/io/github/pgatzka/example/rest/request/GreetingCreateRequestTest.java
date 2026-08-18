package io.github.pgatzka.example.rest.request;

import io.github.pgatzka.example.test.BeanValidationTest;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

class GreetingCreateRequestTest extends BeanValidationTest<GreetingCreateRequest> {

    private static final String VALID_AUTHOR = "Raymond";

    private static final String VALID_MESSAGE = "If horses were wishes, beggars would ride.";

    private static final String VALID_SUBJECT = "Donald";

    static Stream<Arguments> validCases() {
        return Stream.of(
                Arguments.of(new GreetingCreateRequest(VALID_AUTHOR, VALID_MESSAGE, VALID_SUBJECT)),
                Arguments.of(new GreetingCreateRequest(VALID_AUTHOR, "A".repeat(200), VALID_SUBJECT)),
                Arguments.of(new GreetingCreateRequest(VALID_AUTHOR, "", VALID_SUBJECT)),
                Arguments.of(new GreetingCreateRequest(VALID_AUTHOR, null, VALID_SUBJECT))
        );
    }

    static Stream<Arguments> invalidCases() {
        return Stream.of(
                Arguments.of(new GreetingCreateRequest("", VALID_MESSAGE, VALID_SUBJECT), NotBlank.class, "author"),
                Arguments.of(new GreetingCreateRequest(null, VALID_MESSAGE, VALID_SUBJECT), NotBlank.class, "author"),
                Arguments.of(new GreetingCreateRequest("A".repeat(50), VALID_MESSAGE, VALID_SUBJECT), Length.class, "author"),
                Arguments.of(new GreetingCreateRequest(VALID_AUTHOR, "A".repeat(250), VALID_SUBJECT), Length.class, "message"),
                Arguments.of(new GreetingCreateRequest(VALID_AUTHOR, VALID_MESSAGE, null), NotBlank.class, "subject"),
                Arguments.of(new GreetingCreateRequest(VALID_AUTHOR, VALID_MESSAGE, ""), NotBlank.class, "subject"),
                Arguments.of(new GreetingCreateRequest(VALID_AUTHOR, VALID_MESSAGE, "A".repeat(50)), Length.class, "subject")
        );
    }

}

package io.github.pgatzka.example.rest.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class RequestIdFilterTest {

    private static final String HEADER = "X-Request-Id";

    private static final String MDC_KEY = "requestId";

    private static final UUID INBOUND_ID = UUID.randomUUID();

    private final RequestIdFilter filter = new RequestIdFilter();

    private final MockHttpServletRequest request = new MockHttpServletRequest();

    private final MockHttpServletResponse response = new MockHttpServletResponse();

    private final AtomicReference<String> observed = new AtomicReference<>();

    private final FilterChain chain = (_, _) -> observed.set(MDC.get(MDC_KEY));

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Nested
    @DisplayName("with a valid inbound header")
    class InboundHeader {

        @Test
        void reusesTheCallerValue() throws Exception {
            request.addHeader(HEADER, INBOUND_ID);

            filter.doFilter(request, response, chain);

            assertThat(observed.get()).isEqualTo(INBOUND_ID.toString());
            assertThat(response.getHeader(HEADER)).isEqualTo(INBOUND_ID.toString());
        }

    }

    @Nested
    @DisplayName("without a usable inbound header")
    class GeneratedId {

        @Test
        void generatesAUuidWhenTheHeaderIsAbsent() throws Exception {
            filter.doFilter(request, response, chain);

            assertThatCode(() -> UUID.fromString(observed.get())).doesNotThrowAnyException();
            assertThat(response.getHeader(HEADER)).isEqualTo(observed.get());
        }

        @Test
        void generatesAUuidWhenTheHeaderContainsUnsafeCharacters() throws Exception {
            request.addHeader(HEADER, "injected\nWARN forged log line");

            filter.doFilter(request, response, chain);

            assertThat(observed.get()).doesNotContain("forged");
            assertThatCode(() -> UUID.fromString(observed.get())).doesNotThrowAnyException();
        }

        @Test
        void generatesAUuidWhenTheHeaderIsTooLong() throws Exception {
            request.addHeader(HEADER, "a".repeat(65));

            filter.doFilter(request, response, chain);

            assertThatCode(observed::get).doesNotThrowAnyException();
        }

    }

    @Nested
    @DisplayName("MDC lifecycle")
    class Lifecycle {

        @Test
        void removesTheEntryAfterTheRequest() throws Exception {
            filter.doFilter(request, response, chain);

            assertThat(MDC.get(MDC_KEY)).isNull();
        }

        @Test
        void removesTheEntryWhenTheChainThrows() {
            FilterChain failing = (req, res) -> {
                throw new IllegalStateException("downstream failure");
            };

            assertThatCode(() -> filter.doFilter(request, response, failing)).isInstanceOf(IllegalStateException.class);

            assertThat(MDC.get(MDC_KEY)).isNull();
        }

    }

}


package io.github.pgatzka.example.service;

import io.github.pgatzka.example.domain.entity.Greeting;
import io.github.pgatzka.example.domain.mapper.GreetingMapper;
import io.github.pgatzka.example.domain.repository.GreetingRepository;
import io.github.pgatzka.example.exception.GreetingNotFoundException;
import io.github.pgatzka.example.rest.request.GreetingCreateRequest;
import io.github.pgatzka.example.rest.request.UpdateGreetingRequest;
import io.github.pgatzka.example.rest.response.response.GreetingResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GreetingServiceTest {

    private static final UUID UUID_VALUE = UUID.fromString("6b3d1e0c-1d3a-4a7f-9c4e-0a1b2c3d4e5f");

    private static final String AUTHOR = "Raymond";

    private static final String MESSAGE = "If horses were wishes, beggars would ride.";

    private static final String SUBJECT = "Donald";

    private static final GreetingResponse RESPONSE = new GreetingResponse(UUID_VALUE, AUTHOR, MESSAGE, SUBJECT);

    private static final GreetingCreateRequest CREATE_REQUEST = new GreetingCreateRequest(AUTHOR, MESSAGE, SUBJECT);

    private static final UpdateGreetingRequest UPDATE_REQUEST = new UpdateGreetingRequest(AUTHOR, MESSAGE, SUBJECT);

    @Mock
    private GreetingRepository greetingRepository;

    @Mock
    private GreetingMapper greetingMapper;

    @InjectMocks
    private GreetingService service;

    @Nested
    @DisplayName("greet(GreetingCreateRequest)")
    class Greet {

        @Test
        void mapsRequestToEntitySavesItAndReturnsMappedResponse() {
            Greeting entity = new Greeting(AUTHOR, MESSAGE, SUBJECT);
            Greeting saved = new Greeting(AUTHOR, MESSAGE, SUBJECT);
            when(greetingMapper.toEntity(CREATE_REQUEST)).thenReturn(entity);
            when(greetingRepository.save(entity)).thenReturn(saved);
            when(greetingMapper.toResponse(saved)).thenReturn(RESPONSE);

            assertThat(service.greet(CREATE_REQUEST)).isEqualTo(RESPONSE);

            verify(greetingRepository).save(entity);
        }

        @Test
        void returnsMappedResponseWhenGreetingHasNoMessage() {
            GreetingCreateRequest request = new GreetingCreateRequest(AUTHOR, null, SUBJECT);
            GreetingResponse response = new GreetingResponse(UUID_VALUE, AUTHOR, null, SUBJECT);
            Greeting entity = new Greeting(AUTHOR, null, SUBJECT);
            when(greetingMapper.toEntity(request)).thenReturn(entity);
            when(greetingRepository.save(entity)).thenReturn(entity);
            when(greetingMapper.toResponse(entity)).thenReturn(response);

            assertThat(service.greet(request)).isEqualTo(response);
        }

    }

    @Nested
    @DisplayName("get(UUID)")
    class Get {

        @Test
        void returnsMappedResponseOfFoundGreeting() {
            Greeting entity = new Greeting(AUTHOR, MESSAGE, SUBJECT);
            when(greetingRepository.findByUuid(UUID_VALUE)).thenReturn(Optional.of(entity));
            when(greetingMapper.toResponse(entity)).thenReturn(RESPONSE);

            assertThat(service.get(UUID_VALUE)).isEqualTo(RESPONSE);
        }

        @Test
        void throwsGreetingNotFoundWhenGreetingIsAbsent() {
            when(greetingRepository.findByUuid(UUID_VALUE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.get(UUID_VALUE)).isInstanceOf(GreetingNotFoundException.class);

            verifyNoInteractions(greetingMapper);
        }

    }

    @Nested
    @DisplayName("list(Pageable)")
    class ListAll {

        @Test
        void returnsPagedModelOfMappedResponsesForRequestedPage() {
            Pageable pageable = PageRequest.of(0, 20);
            Greeting entity = new Greeting(AUTHOR, MESSAGE, SUBJECT);
            when(greetingRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));
            when(greetingMapper.toResponse(entity)).thenReturn(RESPONSE);

            PagedModel<GreetingResponse> result = service.list(pageable);

            assertThat(result.getContent()).containsExactly(RESPONSE);
            verify(greetingRepository).findAll(pageable);
        }

    }

    @Nested
    @DisplayName("update(UUID, UpdateGreetingRequest)")
    class Update {

        @Test
        void appliesRequestToFoundGreetingSavesItAndReturnsMappedResponse() {
            Greeting entity = new Greeting(AUTHOR, MESSAGE, SUBJECT);
            Greeting updated = new Greeting("Ray", "Wishes", "Don");
            Greeting saved = new Greeting("Ray", "Wishes", "Don");
            when(greetingRepository.findByUuid(UUID_VALUE)).thenReturn(Optional.of(entity));
            when(greetingMapper.updateEntity(UPDATE_REQUEST, entity)).thenReturn(updated);
            when(greetingRepository.save(updated)).thenReturn(saved);
            when(greetingMapper.toResponse(saved)).thenReturn(RESPONSE);

            assertThat(service.update(UUID_VALUE, UPDATE_REQUEST)).isEqualTo(RESPONSE);

            verify(greetingMapper).updateEntity(UPDATE_REQUEST, entity);
            verify(greetingRepository).save(updated);
        }

        @Test
        void throwsGreetingNotFoundAndSavesNothingWhenGreetingIsAbsent() {
            when(greetingRepository.findByUuid(UUID_VALUE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(UUID_VALUE, UPDATE_REQUEST))
                    .isInstanceOf(GreetingNotFoundException.class);

            verify(greetingRepository, never()).save(any());
            verifyNoInteractions(greetingMapper);
        }

    }

    @Nested
    @DisplayName("delete(UUID)")
    class Delete {

        @Test
        void deletesGreetingWhenItExists() {
            when(greetingRepository.existsByUuid(UUID_VALUE)).thenReturn(true);

            service.delete(UUID_VALUE);

            verify(greetingRepository).deleteByUuid(UUID_VALUE);
        }

        @Test
        void throwsGreetingNotFoundAndDeletesNothingWhenGreetingIsAbsent() {
            when(greetingRepository.existsByUuid(UUID_VALUE)).thenReturn(false);

            assertThatThrownBy(() -> service.delete(UUID_VALUE)).isInstanceOf(GreetingNotFoundException.class);

            verify(greetingRepository, never()).deleteByUuid(any());
        }

    }

}

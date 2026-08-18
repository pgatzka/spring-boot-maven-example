package io.github.pgatzka.example.rest.controller;

import io.github.pgatzka.example.rest.request.GreetingCreateRequest;
import io.github.pgatzka.example.rest.request.UpdateGreetingRequest;
import io.github.pgatzka.example.rest.response.response.GreetingResponse;
import io.github.pgatzka.example.service.GreetingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GreetingController.class)
class GreetingControllerTest {

    private static final String BASE_PATH = "/api/v1/greeting";

    private static final UUID UUID_VALUE = UUID.fromString("6b3d1e0c-1d3a-4a7f-9c4e-0a1b2c3d4e5f");

    private static final String AUTHOR = "Raymond";

    private static final String MESSAGE = "If horses were wishes, beggars would ride.";

    private static final String SUBJECT = "Donald";

    private static final GreetingResponse RESPONSE = new GreetingResponse(UUID_VALUE, AUTHOR, MESSAGE, SUBJECT);

    private static final GreetingCreateRequest CREATE_REQUEST = new GreetingCreateRequest(AUTHOR, MESSAGE, SUBJECT);

    private static final UpdateGreetingRequest UPDATE_REQUEST = new UpdateGreetingRequest(AUTHOR, MESSAGE, SUBJECT);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GreetingService service;

    private String json(Object bean) {
        return objectMapper.writeValueAsString(bean);
    }

    @Nested
    @DisplayName("POST /api/v1/greeting/greet")
    class Create {

        @Test
        void returnsCreatedWithLocationAndSerializedGreeting() throws Exception {
            when(service.greet(any())).thenReturn(RESPONSE);

            mockMvc.perform(post(BASE_PATH + "/greet")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(CREATE_REQUEST)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost" + BASE_PATH + "/" + UUID_VALUE))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(json(RESPONSE)));
        }

        @Test
        void passesDeserializedRequestBodyToService() throws Exception {
            when(service.greet(any())).thenReturn(RESPONSE);

            mockMvc.perform(post(BASE_PATH + "/greet")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(CREATE_REQUEST)))
                    .andExpect(status().isCreated());

            ArgumentCaptor<GreetingCreateRequest> captor = ArgumentCaptor.forClass(GreetingCreateRequest.class);
            verify(service).greet(captor.capture());
            assertThat(captor.getValue()).isEqualTo(CREATE_REQUEST);
        }

    }

    @Nested
    @DisplayName("GET /api/v1/greeting/{uuid}")
    class Get {

        @Test
        void returnsOkWithSerializedGreeting() throws Exception {
            when(service.get(UUID_VALUE)).thenReturn(RESPONSE);

            mockMvc.perform(get(BASE_PATH + "/{uuid}", UUID_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(json(RESPONSE)));
        }

        @Test
        void passesPathVariableToService() throws Exception {
            when(service.get(UUID_VALUE)).thenReturn(RESPONSE);

            mockMvc.perform(get(BASE_PATH + "/{uuid}", UUID_VALUE))
                    .andExpect(status().isOk());

            verify(service).get(UUID_VALUE);
        }

    }

    @Nested
    @DisplayName("GET /api/v1/greeting")
    class ListAll {

        @Test
        void returnsOkWithSerializedPage() throws Exception {
            PagedModel<GreetingResponse> page = new PagedModel<>(new PageImpl<>(List.of(RESPONSE), PageRequest.of(0, 20), 1));
            when(service.list(any())).thenReturn(page);

            mockMvc.perform(get(BASE_PATH))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(json(page)));
        }

        @Test
        void passesDefaultPageableToService() throws Exception {
            when(service.list(any())).thenReturn(new PagedModel<>(new PageImpl<>(List.of())));

            mockMvc.perform(get(BASE_PATH))
                    .andExpect(status().isOk());

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(service).list(captor.capture());
            assertThat(captor.getValue().getPageNumber()).isZero();
            assertThat(captor.getValue().getPageSize()).isEqualTo(20);
            assertThat(captor.getValue().getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdAt"));
        }

    }

    @Nested
    @DisplayName("PUT /api/v1/greeting/{uuid}")
    class Update {

        @Test
        void returnsOkWithSerializedGreeting() throws Exception {
            when(service.update(eq(UUID_VALUE), any())).thenReturn(RESPONSE);

            mockMvc.perform(put(BASE_PATH + "/{uuid}", UUID_VALUE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(UPDATE_REQUEST)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(json(RESPONSE)));
        }

        @Test
        void passesPathVariableAndDeserializedRequestBodyToService() throws Exception {
            when(service.update(eq(UUID_VALUE), any())).thenReturn(RESPONSE);

            mockMvc.perform(put(BASE_PATH + "/{uuid}", UUID_VALUE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json(UPDATE_REQUEST)))
                    .andExpect(status().isOk());

            ArgumentCaptor<UpdateGreetingRequest> captor = ArgumentCaptor.forClass(UpdateGreetingRequest.class);
            verify(service).update(eq(UUID_VALUE), captor.capture());
            assertThat(captor.getValue()).isEqualTo(UPDATE_REQUEST);
        }

    }

    @Nested
    @DisplayName("DELETE /api/v1/greeting/{uuid}")
    class Delete {

        @Test
        void returnsNoContentWithEmptyBodyAndPassesPathVariableToService() throws Exception {
            mockMvc.perform(delete(BASE_PATH + "/{uuid}", UUID_VALUE))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(service).delete(UUID_VALUE);
        }

    }

}

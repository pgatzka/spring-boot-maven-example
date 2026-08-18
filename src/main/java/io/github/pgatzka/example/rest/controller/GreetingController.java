package io.github.pgatzka.example.rest.controller;

import io.github.pgatzka.example.rest.response.response.GreetingResponse;
import io.github.pgatzka.example.service.GreetingService;
import io.github.pgatzka.example.rest.request.GreetRequest;
import io.github.pgatzka.example.rest.request.UpdateGreetRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/api/v1/greeting")
@RequiredArgsConstructor
public class GreetingController {

    private final GreetingService service;

    @PostMapping(value = "/greet", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GreetingResponse> greet(@RequestBody @Valid GreetRequest request) {
        GreetingResponse greeting = service.greet(request);
        URI location = MvcUriComponentsBuilder.fromMethodCall(on(GreetingController.class).get(greeting.uuid())).build(1);
        return ResponseEntity.created(location).body(greeting);
    }

    @GetMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GreetingResponse> get(@PathVariable("uuid") UUID uuid) {
        return ResponseEntity.ok(service.get(uuid));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedModel<GreetingResponse>> list(@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @PutMapping(value = "/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GreetingResponse> update(@PathVariable("uuid") UUID uuid, @RequestBody @Valid UpdateGreetRequest request) {
        return ResponseEntity.ok(service.update(uuid, request));
    }

    @DeleteMapping(value = "/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable("uuid") UUID uuid) {
        service.delete(uuid);
        return ResponseEntity.noContent().build();
    }

}

package io.github.pgatzka.example.service;

import io.github.pgatzka.example.domain.mapper.GreetingMapper;
import io.github.pgatzka.example.domain.repository.GreetingRepository;
import io.github.pgatzka.example.rest.response.response.GreetingResponse;
import io.github.pgatzka.example.exception.GreetingNotFoundException;
import io.github.pgatzka.example.rest.request.GreetRequest;
import io.github.pgatzka.example.rest.request.UpdateGreetingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GreetingService {

    private final GreetingRepository greetingRepository;

    private final GreetingMapper greetingMapper;

    @Transactional
    public GreetingResponse greet(GreetRequest request) {
        GreetingResponse response = greetingMapper.toResponse(greetingRepository.save(greetingMapper.toEntity(request)));
        if (response.message() == null) {
            log.info("'{}' greeted '{}' without a message", response.author(), response.subject());
        } else {
            log.info("'{}' greeted '{}' with message '{}'", response.author(), response.subject(), response.message());
        }
        return response;
    }

    public GreetingResponse get(UUID uuid) {
        GreetingResponse response = greetingRepository.findByUuid(uuid).map(greetingMapper::toResponse).orElseThrow(() -> new GreetingNotFoundException(uuid));
        log.info("Fetched greeting: {}", response);
        return response;
    }

    public PagedModel<GreetingResponse> list(Pageable pageable) {
        log.info("Fetching greetings with pageSize: {}, pageNumber: {}", pageable.getPageSize(), pageable.getPageNumber());
        return new PagedModel<>(greetingRepository.findAll(pageable).map(greetingMapper::toResponse));
    }

    @Transactional
    public GreetingResponse update(UUID uuid, UpdateGreetingRequest request) {
        GreetingResponse response = greetingMapper.toResponse(greetingRepository.save(greetingMapper.updateEntity(request, greetingRepository.findByUuid(uuid).orElseThrow(() -> new GreetingNotFoundException(uuid)))));
        log.info("Updated greeting: '{}'", uuid);
        return response;
    }

    @Transactional
    public void delete(UUID uuid) {
        if (!greetingRepository.existsByUuid(uuid)) {
            throw new GreetingNotFoundException(uuid);
        }
        greetingRepository.deleteByUuid(uuid);
        log.info("Deleted greeting '{}'", uuid);
    }
}

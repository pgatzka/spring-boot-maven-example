
package io.github.pgatzka.example.service;

import io.github.pgatzka.example.domain.mapper.GreetingMapper;
import io.github.pgatzka.example.domain.repository.GreetingRepository;
import io.github.pgatzka.example.exception.GreetingNotFoundException;
import io.github.pgatzka.example.rest.request.GreetingCreateRequest;
import io.github.pgatzka.example.rest.request.UpdateGreetingRequest;
import io.github.pgatzka.example.rest.response.response.GreetingResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GreetingService {

    private final GreetingRepository greetingRepository;

    private final GreetingMapper greetingMapper;

    @Transactional
    public GreetingResponse greet(GreetingCreateRequest request) {
        GreetingResponse response = greetingMapper
                        .toResponse(greetingRepository.save(greetingMapper.toEntity(request)));

        log.atInfo().setMessage("Greeting created").addKeyValue("greeting.author", response.author())
                        .addKeyValue("greeting.subject", response.subject())
                        .addKeyValue("greeting.has_message", response.message() != null).log();
        return response;
    }

    public GreetingResponse get(UUID uuid) {
        GreetingResponse response = greetingRepository.findByUuid(uuid).map(greetingMapper::toResponse)
                        .orElseThrow(() -> new GreetingNotFoundException(uuid));
        log.atInfo().setMessage("Fetched greeting").addKeyValue("greeting", response).log();
        return response;
    }

    public PagedModel<GreetingResponse> list(Pageable pageable) {
        log.atInfo().setMessage("Fetching greetings page").addKeyValue("page.page_size", pageable.getPageSize())
                        .addKeyValue("page.page_number", pageable.getPageNumber()).log();
        return new PagedModel<>(greetingRepository.findAll(pageable).map(greetingMapper::toResponse));
    }

    @Transactional
    public GreetingResponse update(UUID uuid, UpdateGreetingRequest request) {
        GreetingResponse response = greetingMapper.toResponse(greetingRepository.save(greetingMapper.updateEntity(
                        request,
                        greetingRepository.findByUuid(uuid).orElseThrow(() -> new GreetingNotFoundException(uuid)))));
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

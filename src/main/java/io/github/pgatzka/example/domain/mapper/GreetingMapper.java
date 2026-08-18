package io.github.pgatzka.example.domain.mapper;

import io.github.pgatzka.example.domain.entity.Greeting;
import io.github.pgatzka.example.rest.response.response.GreetingResponse;
import io.github.pgatzka.example.rest.request.GreetRequest;
import io.github.pgatzka.example.rest.request.UpdateGreetRequest;
import org.mapstruct.*;

@Mapper
public interface GreetingMapper {

    GreetingResponse toResponse(Greeting greeting);

    Greeting toEntity(GreetRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "author", source = "author")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "subject", source = "subject")
    Greeting updateEntity(UpdateGreetRequest updateGreetRequest, @MappingTarget Greeting greeting);

}

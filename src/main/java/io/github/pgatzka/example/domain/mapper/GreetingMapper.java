
package io.github.pgatzka.example.domain.mapper;

import io.github.pgatzka.example.domain.entity.Greeting;
import io.github.pgatzka.example.rest.response.response.GreetingResponse;
import io.github.pgatzka.example.rest.request.GreetingCreateRequest;
import io.github.pgatzka.example.rest.request.UpdateGreetingRequest;
import org.mapstruct.*;

@Mapper
public interface GreetingMapper {

    GreetingResponse toResponse(Greeting greeting);

    Greeting toEntity(GreetingCreateRequest request);

    @BeanMapping(ignoreByDefault = true) @Mapping(target = "author", source = "author") @Mapping(target = "message", source = "message") @Mapping(target = "subject", source = "subject")
    Greeting updateEntity(UpdateGreetingRequest updateGreetingRequest, @MappingTarget Greeting greeting);

}

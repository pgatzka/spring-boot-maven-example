
package io.github.pgatzka.example.rest.exception;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

public abstract class NotFoundException extends ErrorResponseException {

    public NotFoundException(@NonNull String reason) {
        super(HttpStatus.NOT_FOUND);
        setDetail(reason);
    }

}

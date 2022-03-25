package net.ensan.musify.rest;

import net.ensan.musify.api.model.ApiErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ErrorHandlerControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandlerControllerAdvice.class);

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody
    ApiErrorDto handleIllegalArgumentException(
        final Exception exception,
        final ServletWebRequest request
    ) {
        return handleException(
            exception,
            request,
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public @ResponseBody
    ApiErrorDto handleNoSuchElementException(
        final Exception exception,
        final ServletWebRequest request
    ) {
        return handleException(
            exception,
            request,
            HttpStatus.NOT_FOUND
        );
    }

    private ApiErrorDto handleException(
        final Throwable throwable,
        final ServletWebRequest request,
        final HttpStatus httpStatus
    ) {
        return handleException(throwable, request, httpStatus, throwable.getMessage());
    }

    private ApiErrorDto handleException(
        final Throwable throwable,
        final ServletWebRequest request,
        final HttpStatus httpStatus,
        final String message
    ) {
        LOGGER.error(throwable.getMessage(), throwable);

        return new ApiErrorDto()
            .error(message)
            .timestamp(OffsetDateTime.now())
            .status(httpStatus.value())
            .path(request.getRequest().getRequestURI());
    }
}

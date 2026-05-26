package ru.job4j.site.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
import ru.job4j.site.exception.IdNotFoundException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestTemplateResponseErrorHandlerTest {

    private final RestTemplateResponseErrorHandler handler =
            new RestTemplateResponseErrorHandler();

    @Test
    void whenStatusOkThenHasNoError() throws Exception {
        var response = new MockClientHttpResponse(new byte[0], HttpStatus.OK);

        assertFalse(handler.hasError(response));
    }

    @Test
    void whenStatusNotFoundThenHasErrorAndThrowsException() throws Exception {
        var response = new MockClientHttpResponse(new byte[0], HttpStatus.NOT_FOUND);

        assertTrue(handler.hasError(response));
        assertThrows(IdNotFoundException.class, () -> handler.handleError(response));
    }

    @Test
    void whenStatusInternalServerErrorThenHasErrorAndThrowsException() throws Exception {
        var response = new MockClientHttpResponse(
                new byte[0], HttpStatus.INTERNAL_SERVER_ERROR);

        assertTrue(handler.hasError(response));
        assertThrows(IdNotFoundException.class, () -> handler.handleError(response));
    }
}

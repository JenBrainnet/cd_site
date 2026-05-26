package ru.job4j.site.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.job4j.site.exception.IdNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionApiHandlerTest {

    @Test
    void whenIdNotFoundThenReturnNotFoundErrorMessage() {
        var handler = new ExceptionApiHandler();
        var response = handler.notFoundException(
                new IdNotFoundException("Пользователь не найден"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Пользователь не найден");
    }
}

package dev.onioni.bank_api.exception;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ApiExceptionHandlerTest {
    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void handlesBusinessAndNotFoundErrors() {
        assertEquals("bad", handler.badRequest(new BusinessException("bad")).message());
        assertEquals("missing", handler.notFound(new ResourceNotFoundException("missing")).message());
    }

    @Test
    void handlesValidationErrorsWithAndWithoutFieldDetails() {
        BindingResult result = Mockito.mock(BindingResult.class);
        MethodArgumentNotValidException exception = Mockito.mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(result);
        when(result.getFieldErrors()).thenReturn(List.of(new FieldError("request", "amount", "must be positive")));

        assertEquals("amount: must be positive", handler.badRequest(exception).message());

        when(result.getFieldErrors()).thenReturn(List.of());
        assertEquals("Invalid request", handler.badRequest(exception).message());
    }
}

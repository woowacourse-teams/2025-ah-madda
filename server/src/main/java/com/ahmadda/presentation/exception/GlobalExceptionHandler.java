package com.ahmadda.presentation.exception;

import com.ahmadda.application.exception.AccessDeniedException;
import com.ahmadda.application.exception.BusinessFlowViolatedException;
import com.ahmadda.application.exception.NotFoundException;
import com.ahmadda.domain.exception.BusinessRuleViolatedException;
import com.ahmadda.domain.exception.UnauthorizedOperationException;
import com.ahmadda.infra.login.jwt.exception.InvalidJwtException;
import com.ahmadda.infra.notification.push.exception.InvalidFcmRegistrationTokenException;
import com.ahmadda.infra.oauth.exception.InvalidOauthTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleInternalServerError(final Exception ex, final WebRequest request) {
        log.error("InternalServerError: {}", ex.getMessage(), ex);
        ProblemDetail body = super.createProblemDetail(
                ex,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "서버에서 알 수 없는 오류가 발생했습니다.",
                null,
                null,
                request
        );

        return super.handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler({BusinessRuleViolatedException.class, BusinessFlowViolatedException.class})
    public ResponseEntity<Object> handleUnprocessableEntity(final Exception ex, final WebRequest request) {
        ProblemDetail body =
                super.createProblemDetail(ex, HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), null, null, request);

        return super.handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(final Exception ex, final WebRequest request) {
        ProblemDetail body = super.createProblemDetail(ex, HttpStatus.NOT_FOUND, ex.getMessage(), null, null, request);

        return super.handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({InvalidJwtException.class, InvalidOauthTokenException.class, InvalidAuthorizationException.class})
    public ResponseEntity<Object> handleUnauthorized(final Exception ex, final WebRequest request) {
        ProblemDetail body =
                super.createProblemDetail(ex, HttpStatus.UNAUTHORIZED, ex.getMessage(), null, null, request);

        return super.handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({AccessDeniedException.class, UnauthorizedOperationException.class})
    public ResponseEntity<Object> handleForbidden(final Exception ex, final WebRequest request) {
        ProblemDetail body = super.createProblemDetail(ex, HttpStatus.FORBIDDEN, ex.getMessage(), null, null, request);

        return super.handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(InvalidFcmRegistrationTokenException.class)
    public ResponseEntity<Object> handleBadRequest(final Exception ex, final WebRequest request) {
        ProblemDetail body =
                super.createProblemDetail(ex, HttpStatus.BAD_REQUEST, ex.getMessage(), null, null, request);

        return super.handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException methodArgumentNotValidException,
            final HttpHeaders httpHeaders,
            final HttpStatusCode httpStatusCode,
            final WebRequest request
    ) {
        String validationErrorMessage = methodArgumentNotValidException.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatusCode, validationErrorMessage);

        return super.handleExceptionInternal(
                methodArgumentNotValidException,
                problemDetail,
                httpHeaders,
                httpStatusCode,
                request
        );
    }
}

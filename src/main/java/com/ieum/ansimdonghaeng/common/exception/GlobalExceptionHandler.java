package com.ieum.ansimdonghaeng.common.exception;

import com.ieum.ansimdonghaeng.common.response.ApiResponse;
import com.ieum.ansimdonghaeng.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException exception,
                                                                   HttpServletRequest request) {
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode,
                exception.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.error(errorResponse));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException exception,
                                                                       HttpServletRequest request) {
        String message = Optional.ofNullable(exception.getBindingResult().getFieldError())
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .orElse(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, message, request.getRequestURI());
        return ResponseEntity.badRequest().body(ApiResponse.error(errorResponse));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException exception,
                                                                 HttpServletRequest request) {
        String message = Optional.ofNullable(exception.getBindingResult().getFieldError())
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .orElse(ErrorCode.INVALID_INPUT_VALUE.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, message, request.getRequestURI());
        return ResponseEntity.badRequest().body(ApiResponse.error(errorResponse));
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(Exception exception,
                                                                         HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.FORBIDDEN,
                ErrorCode.FORBIDDEN.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(ErrorCode.FORBIDDEN.getStatus()).body(ApiResponse.error(errorResponse));
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            UsernameNotFoundException.class,
            AuthenticationCredentialsNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(Exception exception,
                                                                           HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.INVALID_CREDENTIALS,
                ErrorCode.INVALID_CREDENTIALS.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(ErrorCode.INVALID_CREDENTIALS.getStatus()).body(ApiResponse.error(errorResponse));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException exception,
                                                                            HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.INVALID_INPUT_VALUE,
                exception.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(ApiResponse.error(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception exception,
                                                                       HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.internalServerError().body(ApiResponse.error(errorResponse));
    }
}

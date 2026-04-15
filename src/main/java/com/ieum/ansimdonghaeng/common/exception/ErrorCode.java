package com.ieum.ansimdonghaeng.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_400", "Invalid request input."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "Authentication is required."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "Access is denied."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_1", "Invalid or expired token."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "Requested resource was not found."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "AUTH_409", "Email is already registered."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_401", "Invalid email or password."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_401_REFRESH", "Refresh token is invalid or expired."),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "AUTH_403", "User account is inactive."),
    OAUTH_EMAIL_NOT_PROVIDED(HttpStatus.BAD_REQUEST, "AUTH_400_OAUTH_EMAIL", "OAuth provider did not provide an email."),
    OAUTH_ACCOUNT_CONFLICT(HttpStatus.CONFLICT, "AUTH_409_OAUTH_ACCOUNT", "Email is already registered with another auth provider."),
    OAUTH_PROVIDER_ERROR(HttpStatus.BAD_GATEWAY, "AUTH_502_OAUTH_PROVIDER", "OAuth provider request failed."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "Unexpected server error.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

package com.work.rest.project.murza.exception;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ControllerAdvisor {

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(HttpStatus status, String message, List<ApiErrorResponse.ErrorDetail> errors) {
        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                message,
                errors
        );
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ApiErrorResponse.ErrorDetail> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ApiErrorResponse.ErrorDetail(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Database integrity violation: {}", ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("database", ex.getMostSpecificCause().getMessage());
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Database error",
                List.of(errorDetail)
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("userId", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "User not found", List.of(errorDetail));
    }

    @ExceptionHandler(ParcelRequestNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleParcelRequestNotFoundException(ParcelRequestNotFoundException ex) {
        log.warn("Parcel request not found: {}", ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("parcelId", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Parcel request not found", List.of(errorDetail));
    }

    @ExceptionHandler(ParcelNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleParcelNotFoundException(ParcelNotFoundException ex) {
        log.warn("Parcel not found: {}", ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("parcelId", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Parcel not found", List.of(errorDetail));
    }


    @ExceptionHandler(VerificationCodeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleVerificationCodeNotFoundException(VerificationCodeNotFoundException ex) {
        log.warn(ex.getMessage());

        Map<String, Object> additionalData = ex.getAdditionalData();

        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("VerificationCode", ex.getMessage());

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Verification code error",
                List.of(errorDetail),
                additionalData
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

    @ExceptionHandler(VerificationBlockedException.class)
    public ResponseEntity<ApiErrorResponse> handleVerificationBlockedException(VerificationBlockedException ex) {
        log.warn(ex.getMessage());
        Map<String, Object> additionalData = ex.getAdditionalData();
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("Verification Blocked Exception ", ex.getMessage());
        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Too many verification attempts. Please try again later",
                List.of(errorDetail),
                additionalData
        );
        return ResponseEntity.status(HttpStatus.LOCKED).body(response);
    }


    @ExceptionHandler(CaptchaVerificationException.class)
    public ResponseEntity<ApiErrorResponse> handleCaptchaVerificationException(CaptchaVerificationException ex) {
        log.warn(ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("Captcha Verification Exception ", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of(errorDetail));
    }

    @ExceptionHandler(VerificationCodeAlreadyActiveException.class)
    public ResponseEntity<ApiErrorResponse> handleVerificationCodeAlreadyActiveException(VerificationCodeAlreadyActiveException ex) {
        log.warn(ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("Verification Code Already Active Exception ", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Verification Code Already Active", List.of(errorDetail));
    }

    @ExceptionHandler(ParcelRequestAlreadyRealizedException.class)
    public ResponseEntity<ApiErrorResponse> handleParcelRequestAlreadyRealizedException(ParcelRequestAlreadyRealizedException ex) {
        log.warn(ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("Parcel request already realized Exception ", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Parcel request already realized", List.of(errorDetail));
    }

    @ExceptionHandler(SmsSendingException.class)
    public ResponseEntity<ApiErrorResponse> handleSmsSendingException(SmsSendingException ex) {
        log.warn(ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("Sms Sending Exception ", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Sms Sending Exception", List.of(errorDetail));
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailSendingException(EmailSendingException ex) {
        log.warn(ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("Email Sending Exception ", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Email Sending Exception", List.of(errorDetail));
    }

    @ExceptionHandler(VerificationCodeExpiredException.class)
    public ResponseEntity<ApiErrorResponse> handleVerificationCodeExpiredException(VerificationCodeExpiredException ex) {
        log.warn(ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("VerificationCodeId expired", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Verification code expiration error", List.of(errorDetail));
    }

    @ExceptionHandler(SubscribeException.class)
    public ResponseEntity<ApiErrorResponse> handleSubscribeException(SubscribeException ex) {
        log.warn(ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("subscriptionId", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Subscription error", List.of(errorDetail));
    }

    @ExceptionHandler(TripRequestNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTripRequestNotFoundException(TripRequestNotFoundException ex) {
        log.warn("Trip request not found: {}", ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("tripRequestId", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Trip request not found", List.of(errorDetail));
    }

    /**
     * Обработка исключений аутентификации и авторизации
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Bad credentials: {}", ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("authentication", "Invalid email or password");
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication failed", List.of(errorDetail));
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountStatusException(AccountStatusException ex) {
        log.warn("Account status exception: {}", ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("account", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Account issue", List.of(errorDetail));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("authorization", "You are not authorized to access this resource");
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Access denied", List.of(errorDetail));
    }

    /**
     * Обработка JWT исключений
     */
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiErrorResponse> handleSignatureException(SignatureException ex) {
        log.warn("Invalid JWT signature: {}", ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("jwt", "The JWT signature is invalid");
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid JWT token", List.of(errorDetail));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiErrorResponse> handleExpiredJwtException(ExpiredJwtException ex) {
        log.warn("Expired JWT token: {}", ex.getMessage());
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail("jwt", "The JWT token has expired");
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Expired JWT token", List.of(errorDetail));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllExceptions(Exception ex) {
        String errorType = ex.getClass().getSimpleName();
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        ApiErrorResponse.ErrorDetail errorDetail = new ApiErrorResponse.ErrorDetail(errorType, ex.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", List.of(errorDetail));
    }
}

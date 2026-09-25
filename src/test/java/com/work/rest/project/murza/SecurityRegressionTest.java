package com.work.rest.project.murza;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.rest.project.murza.controller.AuthenticationController;
import com.work.rest.project.murza.configuration.JwtHandshakeInterceptor;
import com.work.rest.project.murza.dto.auth.RegisterUserDto;
import com.work.rest.project.murza.entity.RoleEnum;
import com.work.rest.project.murza.dto.auth.ForgotPasswordRequest;
import com.work.rest.project.murza.dto.auth.ResetPasswordRequest;
import com.work.rest.project.murza.entity.VerificationCode;
import com.work.rest.project.murza.exception.VerificationCodeNotFoundException;
import com.work.rest.project.murza.entity.Requests.ParcelRequest;
import com.work.rest.project.murza.entity.Requests.TripRequest;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.repository.ParcelRequestRepository;
import com.work.rest.project.murza.repository.TripRequestRepository;
import com.work.rest.project.murza.service.UserService;
import com.work.rest.project.murza.service.AuthenticationService;
import com.work.rest.project.murza.service.BlacklistJwtService;
import com.work.rest.project.murza.service.settings.VerificationService;
import com.work.rest.project.murza.service.impl.requests.ParcelRequestServiceImpl;
import com.work.rest.project.murza.service.impl.requests.TripRequestServiceImpl;
import com.work.rest.project.murza.service.security.JwtService;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.repository.RoleRepository;
import com.work.rest.project.murza.repository.VerificationCodeRepository;
import com.work.rest.project.murza.service.UserDetailsService;
import com.work.rest.project.murza.service.impl.AuthenticationServiceImpl;
import com.work.rest.project.murza.service.impl.settings.VerificationServiceImpl;
import com.work.rest.project.murza.service.settings.EmailService;
import com.work.rest.project.murza.service.settings.SmsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SecurityRegressionTest {

    @Test
    void userPasswordIsNeverSerialized() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setPassword("secret-hash");
        user.setEmail("private@example.test");
        user.setPhoneNumber("+1234567890");
        String json = new ObjectMapper().writeValueAsString(user);
        assertFalse(json.contains("secret-hash"));
        assertFalse(json.contains("password"));
        assertFalse(json.contains("private@example.test"));
        assertFalse(json.contains("+1234567890"));
    }

    @Test
    void publicRegistrationAlwaysCreatesARegularUser() {
        AuthenticationService authentication = mock(AuthenticationService.class);
        AuthenticationController controller = new AuthenticationController(
                authentication, mock(UserService.class), mock(VerificationService.class));
        RegisterUserDto request = new RegisterUserDto();
        request.setRole(RoleEnum.SUPER_ADMIN);

        ReflectionTestUtils.invokeMethod(controller, "registration", request);

        verify(authentication).signUp(argThat(dto -> dto.getRole() == RoleEnum.USER));
    }

    @Test
    void revokedTokenCannotOpenChatConnection() {
        JwtService jwt = mock(JwtService.class);
        UserRepository users = mock(UserRepository.class);
        BlacklistJwtService blacklist = mock(BlacklistJwtService.class);
        JwtHandshakeInterceptor interceptor = new JwtHandshakeInterceptor(jwt, users, blacklist);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/ws/chat");
        request.setQueryString("token=revoked");
        when(blacklist.isTokenBlacklisted("revoked")).thenReturn(true);

        assertFalse(interceptor.beforeHandshake(new ServletServerHttpRequest(request), null, null, new HashMap<>()));
        verifyNoInteractions(jwt, users);
    }

    @Test
    void resetValidationOnlyAcceptsPasswordResetTokens() {
        VerificationCodeRepository codes = mock(VerificationCodeRepository.class);
        VerificationServiceImpl service = new VerificationServiceImpl(
                codes, mock(UserRepository.class), mock(EmailService.class), mock(SmsService.class));

        assertFalse(service.isCodeValid("activation-token"));
        verify(codes).findByCodeAndType("activation-token", "password-reset");
    }

    @Test
    void forgotPasswordUsesServerUrlAndRejectsInvalidResetToken() {
        UserRepository users = mock(UserRepository.class);
        VerificationCodeRepository codes = mock(VerificationCodeRepository.class);
        VerificationService verification = mock(VerificationService.class);
        EmailService email = mock(EmailService.class);
        AuthenticationServiceImpl service = new AuthenticationServiceImpl(
                mock(PasswordEncoder.class), users, mock(RoleRepository.class), codes,
                mock(AuthenticationManager.class), mock(UserDetailsService.class), mock(JwtService.class),
                mock(BlacklistJwtService.class), verification, email);
        ReflectionTestUtils.setField(service, "frontendBaseUrl", "http://172.90.90.185");
        User user = user(1L);
        user.setEmail("test@example.test");
        when(users.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(verification.generateVerificationCode(user, "password-reset"))
                .thenReturn(VerificationCode.builder().user(user).type("password-reset").build());
        ForgotPasswordRequest forgot = new ForgotPasswordRequest();
        forgot.setEmail(user.getEmail());

        service.forgotPassword(forgot);

        verify(email).sendEmail(eq(user.getEmail()), eq("Reset password"),
                argThat(body -> body.contains("http://172.90.90.185/reset-password?token=")));
        verify(codes).save(argThat(code -> code.getType().equals("password-reset") && code.getCode().length() == 36));

        ResetPasswordRequest reset = new ResetPasswordRequest();
        reset.setToken("activation-token");
        reset.setNewPassword("new-password");
        assertThrows(VerificationCodeNotFoundException.class, () -> service.resetPassword(reset));
        verify(users, never()).save(any(User.class));
    }

    @Test
    void parcelOwnerIsRequiredForDeletionAndCompletion() {
        ParcelRequestRepository repository = mock(ParcelRequestRepository.class);
        UserService users = mock(UserService.class);
        ParcelRequestServiceImpl service = new ParcelRequestServiceImpl(repository, null, null, users);
        UUID id = UUID.randomUUID();
        ParcelRequest request = new ParcelRequest();
        request.setSender(user(2L));
        when(repository.findById(id)).thenReturn(Optional.of(request));
        when(users.getCurrentUser()).thenReturn(user(1L));

        assertThrows(AccessDeniedException.class, () -> service.deleteParcelRequest(id));
        assertThrows(AccessDeniedException.class, () -> service.markAsRealized(id));
        verify(repository, never()).delete(any(ParcelRequest.class));
        verify(repository, never()).save(any(ParcelRequest.class));
    }

    @Test
    void tripOwnerIsRequiredForDeletionAndCompletion() {
        TripRequestRepository repository = mock(TripRequestRepository.class);
        UserService users = mock(UserService.class);
        TripRequestServiceImpl service = new TripRequestServiceImpl(null, repository, null, null, users);
        TripRequest request = new TripRequest();
        request.setDriver(user(2L));
        when(repository.findById(5L)).thenReturn(Optional.of(request));
        when(users.getCurrentUser()).thenReturn(user(1L));

        assertThrows(AccessDeniedException.class, () -> service.deleteTripRequest(5L));
        assertThrows(AccessDeniedException.class, () -> service.markAsRealized(5L));
        verify(repository, never()).delete(any(TripRequest.class));
        verify(repository, never()).save(any(TripRequest.class));
    }

    private static User user(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}

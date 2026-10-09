package com.work.rest.project.murza;

import com.work.rest.project.murza.configuration.ChatChannelInterceptor;
import com.work.rest.project.murza.controller.profile.SubscriptionController;
import com.work.rest.project.murza.dto.profile.*;
import com.work.rest.project.murza.dto.request.parcel.CreateParcelRequestDTO;
import com.work.rest.project.murza.entity.*;
import com.work.rest.project.murza.repository.*;
import com.work.rest.project.murza.service.*;
import com.work.rest.project.murza.service.impl.*;
import com.work.rest.project.murza.service.impl.settings.VerificationServiceImpl;
import com.work.rest.project.murza.service.security.JwtService;
import com.work.rest.project.murza.service.settings.*;
import com.work.rest.project.murza.service.utils.FileService;
import com.work.rest.project.murza.exception.VerificationBlockedException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountRegressionTest {
    private User user() {
        User user = new User(); user.setId(11L); user.setFirstName("Murza"); user.setLastName("Traveller");
        user.setEmail("before@example.test"); user.setPhoneNumber("+48123456789"); user.setPassword("hash");
        user.setVerificationStatusEmail(true); user.setVerificationStatusPhone(true); return user;
    }
    private JwtService jwt() {
        JwtService jwt = new JwtService(); jwt.setJwtSecretKey(Base64.getEncoder().encodeToString(new byte[32]));
        jwt.setJwtExpirationInMs(60000); jwt.setRefreshExpirationInMs(120000); return jwt;
    }
    @Test void invalidAccessTokenGets401SoTheClientCanRefreshInsteadOfA403Loop() throws Exception {
        var jwt = mock(JwtService.class);
        var details = mock(org.springframework.security.core.userdetails.UserDetailsService.class);
        when(jwt.extractUsername("refresh")).thenReturn("before@example.test");
        when(details.loadUserByUsername("before@example.test")).thenReturn(user());
        var filter = new com.work.rest.project.murza.service.security.JwtAuthenticationFilter(
                mock(org.springframework.web.servlet.HandlerExceptionResolver.class), jwt, details, mock(BlacklistJwtService.class));
        var request = new org.springframework.mock.web.MockHttpServletRequest(); request.addHeader("Authorization", "Bearer refresh");
        var response = new org.springframework.mock.web.MockHttpServletResponse(); var chain = mock(jakarta.servlet.FilterChain.class);
        ReflectionTestUtils.invokeMethod(filter, "doFilterInternal", request, response, chain);
        assertEquals(401, response.getStatus()); verifyNoInteractions(chain);
    }
    @Test void existingTokensWithoutAVersionKeepWorkingUntilCredentialsChange() {
        User person = user(); JwtService jwt = jwt();
        String legacy = io.jsonwebtoken.Jwts.builder().subject(person.getEmail()).expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(new byte[32])).compact();
        assertTrue(jwt.isTokenValid(legacy, person)); person.setCredentialsVersion(1); assertFalse(jwt.isTokenValid(legacy, person));
    }
    @Test void passwordChangesInvalidateBothTokenTypesAndRefreshCannotAuthenticateRequests() {
        User user = user(); JwtService jwt = jwt(); String access = jwt.generateToken(user), refresh = jwt.generateRefreshToken(user);
        assertTrue(jwt.isTokenValid(access, user)); assertTrue(jwt.isRefreshTokenValid(refresh, user));
        assertFalse(jwt.isTokenValid(refresh, user)); assertFalse(jwt.isRefreshTokenValid(access, user));
        user.setCredentialsVersion(1);
        assertFalse(jwt.isTokenValid(access, user)); assertFalse(jwt.isRefreshTokenValid(refresh, user));
        assertTrue(jwt.isTokenValid(jwt.generateToken(user), user));
    }
    @Test void credentialsChangeAlsoRevokesAnExistingSocketWithItsStaleUserSnapshot() {
        User snapshot = user(), current = user(); JwtService jwt = jwt(); String token = jwt.generateToken(snapshot); current.setCredentialsVersion(1);
        UserRepository users = mock(UserRepository.class); when(users.findById(11L)).thenReturn(Optional.of(current));
        var headers = StompHeaderAccessor.create(StompCommand.SEND); headers.setDestination("/app/chat.readReceipt");
        headers.setUser(() -> snapshot.getEmail()); headers.setSessionAttributes(Map.of("accessToken", token, "authenticatedUser", snapshot)); headers.setLeaveMutable(true);
        assertThrows(AccessDeniedException.class, () -> new ChatChannelInterceptor(jwt, mock(BlacklistJwtService.class), users)
                .preSend(MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders()), null));
    }
    @Test void contactChangesResetVerificationAndDestroyCodesForTheOldDestination() {
        User person = user(); UserRepository users = mock(UserRepository.class); PasswordEncoder encoder = mock(PasswordEncoder.class);
        VerificationCodeRepository codes = mock(VerificationCodeRepository.class);
        when(users.findById(11L)).thenReturn(Optional.of(person)); when(encoder.matches("current", "hash")).thenReturn(true);
        var service = new UserServiceImpl(users, encoder, mock(FileService.class), mock(AuthenticationService.class), codes);
        var result = service.updateUserSettings(11L, UserSettingsUpdateDto.builder().email("after@example.test").phoneNumber("+48987654321").currentPassword("current").build());
        assertFalse(result.isEmailVerified()); assertFalse(result.isPhoneVerified()); assertEquals(1, person.getCredentialsVersion());
        verify(codes).deleteByUserAndType(person, "email"); verify(codes).deleteByUserAndType(person, "phone"); verify(codes).deleteByUserAndType(person, "password-reset");
    }
    @Test void wrongPasswordIsAClientErrorAndDoesNotWriteAnything() {
        UserRepository users = mock(UserRepository.class); VerificationCodeRepository codes = mock(VerificationCodeRepository.class);
        when(users.findById(11L)).thenReturn(Optional.of(user()));
        var service = new UserServiceImpl(users, mock(PasswordEncoder.class), mock(FileService.class), mock(AuthenticationService.class), codes);
        var error = assertThrows(ResponseStatusException.class, () -> service.updateUserSettings(11L, UserSettingsUpdateDto.builder().currentPassword("wrong").email("new@example.test").build()));
        assertEquals(400, error.getStatusCode().value()); verify(users, never()).save(any()); verifyNoInteractions(codes);
    }
    @Test void removingAFollowerDeletesTheIncomingRelationshipAndUsesTheAuthenticatedOwner() {
        User owner = user(), follower = new User(); follower.setId(22L);
        Subscription relationship = new Subscription(); relationship.setFollower(follower); relationship.setFollowed(owner);
        SubscriptionRepository repository = mock(SubscriptionRepository.class); UserService users = mock(UserService.class);
        when(users.getUserById(11L)).thenReturn(owner); when(users.getUserById(22L)).thenReturn(follower);
        when(repository.findByFollowerAndFollowed(follower, owner)).thenReturn(Optional.of(relationship));
        new SubscriptionController(new SubscriptionServiceImpl(repository, users)).removeFollower(22L, owner);
        verify(repository).delete(relationship); verify(repository, never()).findByFollowerAndFollowed(owner, follower);
    }
    @Test void cannotSubscribeToYourself() {
        SubscriptionRepository repository = mock(SubscriptionRepository.class); UserService users = mock(UserService.class);
        assertThrows(ResponseStatusException.class, () -> new SubscriptionServiceImpl(repository, users).subscribe(11L, 11L)); verifyNoInteractions(repository, users);
    }
    @Test void blockedVerificationRejectsEvenTheCorrectCode() {
        VerificationCodeRepository codes = mock(VerificationCodeRepository.class); UserRepository users = mock(UserRepository.class);
        VerificationCode code = VerificationCode.builder().code("123456").expirationTime(new Date(System.currentTimeMillis() + 60000)).user(user()).build();
        code.setBlockedUntil(new Date(System.currentTimeMillis() + 60000));
        when(codes.findLatestUnverifiedCodeWithinTimeframeNative(11L, "email")).thenReturn(Optional.of(code));
        var service = new VerificationServiceImpl(codes, users, mock(EmailService.class), mock(SmsService.class));
        assertThrows(VerificationBlockedException.class, () -> service.verifyCode(11L, "123456", "email")); verify(codes, never()).save(any()); verifyNoInteractions(users);
    }
    @Test void invalidImageInASelectionPreventsAllUploads() throws Exception {
        S3Client s3 = mock(S3Client.class); var service = new FileServiceImpl(s3);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(); ImageIO.write(new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB), "png", bytes);
        var real = new MockMultipartFile("files", "real.png", "image/png", bytes.toByteArray());
        var fake = new MockMultipartFile("files", "fake.png", "image/png", "<svg onload='alert(1)'/>".getBytes());
        assertThrows(ResponseStatusException.class, () -> service.saveFiles(List.of(real, fake), "test/", "1")); verifyNoInteractions(s3);
    }
    @Test void photoUploadsGetUniqueKeysCorrectMimeTypeAndSmallerDimensions() throws Exception {
        S3Client s3 = mock(S3Client.class); var service = new FileServiceImpl(s3);
        ReflectionTestUtils.setField(service, "bucketName", "test-bucket"); ReflectionTestUtils.setField(service, "region", "eu-north-1");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(); ImageIO.write(new BufferedImage(2000, 10, BufferedImage.TYPE_INT_ARGB), "png", bytes);
        var file = new MockMultipartFile("files", "picture.svg", "text/html", bytes.toByteArray());
        var urls = service.saveFiles(List.of(file, file), "test/", "1"); assertNotEquals(urls.get(0), urls.get(1));
        var request = ArgumentCaptor.forClass(PutObjectRequest.class); var body = ArgumentCaptor.forClass(RequestBody.class);
        verify(s3, times(2)).putObject(request.capture(), body.capture()); assertEquals("image/png", request.getValue().contentType());
        try (var input = body.getValue().contentStreamProvider().newStream()) {assertEquals(1600, ImageIO.read(input).getWidth());}
    }
    @Test void photoCleanupCannotRemoveAnotherUsersPicturesOrFollowExternalUrls() {
        S3Client s3 = mock(S3Client.class); var service = new FileServiceImpl(s3);
        ReflectionTestUtils.setField(service, "bucketName", "test-bucket"); ReflectionTestUtils.setField(service, "region", "eu-north-1");
        service.deleteStoredPhotos(List.of("https://test-bucket.s3.eu-north-1.amazonaws.com/profile-pictures/22/other.png",
                "https://evil.example/profile-pictures/11/test.png", "https://test-bucket.s3.eu-north-1.amazonaws.com/profile-pictures/11/%2E%2E/private.png",
                "https://test-bucket.s3.eu-north-1.amazonaws.com/profile-pictures/11/own.png"), "profile-pictures/11/");
        var request = ArgumentCaptor.forClass(software.amazon.awssdk.services.s3.model.DeleteObjectRequest.class);
        verify(s3).deleteObject(request.capture()); assertEquals("profile-pictures/11/own.png", request.getValue().key());
        verifyNoMoreInteractions(s3);
    }
    @Test void paginationRejectsHugeResponsesAndArbitraryEntityPaths() {
        assertThrows(ResponseStatusException.class, () -> RequestPagination.page(0, 100000, org.springframework.data.domain.Sort.unsorted()));
        assertThrows(ResponseStatusException.class, () -> RequestPagination.sort("sender.password", "ASC", "pickupDate", Set.of("pickupDate")));
    }
    @Test void dateOrderIsValidatedOnTheServerAndProfileUsesPickupAsDeparture() {
        var request = new CreateParcelRequestDTO(); request.setPickupDate(new Date(2000)); request.setDeliveryDate(new Date(1000));
        try (var factory = Validation.buildDefaultValidatorFactory()) {assertTrue(factory.getValidator().validate(request).stream().anyMatch(v -> v.getPropertyPath().toString().equals("dateOrderValid")));}
        var parcel = new com.work.rest.project.murza.entity.Requests.ParcelRequest(); parcel.setPickupDate(new Date(1000)); parcel.setDeliveryDate(new Date(2000));
        var profile = com.work.rest.project.murza.mapper.ParcelMapper.parcelRequestToUserParcelDto(parcel);
        assertEquals(parcel.getPickupDate(), profile.getDepartureDate()); assertEquals(parcel.getDeliveryDate(), profile.getArrivalDate());
    }
}

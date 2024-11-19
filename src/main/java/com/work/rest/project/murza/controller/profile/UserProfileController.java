package com.work.rest.project.murza.controller.profile;


import com.work.rest.project.murza.dto.profile.UserProfileDto;
import com.work.rest.project.murza.dto.profile.UserProfileUpdateDto;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@Validated
@CrossOrigin
public class UserProfileController {

    private final UserService userProfileService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getMyProfile(@AuthenticationPrincipal User currentUser) {
        UserProfileDto profileDto = userProfileService.getUserProfile(currentUser.getId(), true);
        return ResponseEntity.ok(profileDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUserProfile(@AuthenticationPrincipal User currentUser, @PathVariable Long id) {
        boolean isOwner = currentUser != null && currentUser.getId().equals(id);
        UserProfileDto profileDto = userProfileService.getUserProfile(id, isOwner);
        return ResponseEntity.ok(profileDto);
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileDto> updateMyProfile(@AuthenticationPrincipal User currentUser,
                                                          @ModelAttribute @Valid UserProfileUpdateDto userProfileDto) {
        UserProfileDto updatedProfile = userProfileService.updateUserProfile(currentUser.getId(), userProfileDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedProfile);
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<String> getUserStatus(@PathVariable Long id) {
        String status = userProfileService.getUserStatus(id);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/is-owner/{id}")
    public ResponseEntity<Boolean> isOwner(@AuthenticationPrincipal User currentUser, @PathVariable Long id) {
        boolean isOwner = currentUser.getId().equals(id);
        return ResponseEntity.ok(isOwner);
    }

   /* @PostMapping("/{id}/review")
    public ResponseEntity<Void> addReview(@AuthenticationPrincipal User currentUser, @PathVariable Long id, @RequestBody String review, @RequestParam int rating) {
        userProfileService.addReview(id, currentUser.getId(), review, rating);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
*/
}

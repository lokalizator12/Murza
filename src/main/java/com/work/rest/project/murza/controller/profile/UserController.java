package com.work.rest.project.murza.controller.profile;

import com.work.rest.project.murza.dto.profile.UserSettingsDto;
import com.work.rest.project.murza.dto.profile.UserSettingsUpdateDto;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RequestMapping("/api/v1/user/")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/settings/update-credentials")
    public ResponseEntity<UserSettingsDto> updateUserSettings(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UserSettingsUpdateDto userSettingsUpdateDto) {
        UserSettingsDto userSettingsDto = userService.updateUserSettings(currentUser.getId(), userSettingsUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(userSettingsDto);
    }

    @GetMapping("/settings")
    public ResponseEntity<UserSettingsDto> getUserSettings(@AuthenticationPrincipal User currentUser) {
        UserSettingsDto userSettingsDto = userService.getUserSettings(currentUser.getId());
        return ResponseEntity.ok(userSettingsDto);
    }

}

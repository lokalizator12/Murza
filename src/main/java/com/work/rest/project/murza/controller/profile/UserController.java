package com.work.rest.project.murza.controller.profile;

import com.work.rest.project.murza.dto.profile.UserSettingsDto;
import com.work.rest.project.murza.dto.profile.UserSettingsUpdateDto;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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

    private final UserRepository userRepository;

    @PostMapping("/updateLastSeen")
    public ResponseEntity<Void> updateLastSeen(Principal principal) {
        String username = principal.getName();
        userService.updateLastSeen(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/contacts")
    public ResponseEntity<List<User>> getContacts(@AuthenticationPrincipal User currentUser) {
        // Return all users except the current user
        List<User> users = userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
}

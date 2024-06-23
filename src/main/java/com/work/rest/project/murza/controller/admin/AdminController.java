package com.work.rest.project.murza.controller.admin;


import com.work.rest.project.murza.dto.RegisterUserDto;
import com.work.rest.project.murza.entity.User;
import com.work.rest.project.murza.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<User> createAdministrator(@Valid @RequestBody RegisterUserDto registerUserDto) {
        log.info("Attempt to create a new administrator with email: {}", registerUserDto.getEmail());
        User createdAdmin = userService.createProfileAdmin(registerUserDto);
        log.info("Successfully created administrator with email: {}", createdAdmin.getEmail());

        return ResponseEntity.ok(createdAdmin);
    }
}

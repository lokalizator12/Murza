package com.work.rest.project.murza.bootstrap;

import com.work.rest.project.murza.dto.auth.RegisterUserDto;
import com.work.rest.project.murza.entity.Role;
import com.work.rest.project.murza.entity.RoleEnum;
import com.work.rest.project.murza.repository.RoleRepository;
import com.work.rest.project.murza.repository.UserRepository;
import com.work.rest.project.murza.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@DependsOn("roleBoot")
@RequiredArgsConstructor
public class AdminBoot implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    @Value("${credentials.super.admin.password}")
    private String superAdminPassword;

    @Value("${credentials.super.admin.email}")
    private String superAdminEmail;

    private static final String SUPER_ADMIN_FIRST_NAME = "Super";
    private static final String SUPER_ADMIN_LAST_NAME = "Admin";
    private static final String SUPER_ADMIN_PHONE = "0000000000";

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        createSuperAdminIfNotExists();
    }

    private void createSuperAdminIfNotExists() {
        log.info("Checking for existence of super admin");

        if (!isSuperAdminConfigurationValid()) {
            log.error("Super admin configuration is invalid. Please check email and password properties.");
            return;
        }

        Role superAdminRole = findSuperAdminRole();

        if (userRepository.existsByRole(superAdminRole)) {
            log.info("Super admin already exists");
            return;
        }

        createSuperAdmin(superAdminRole);
    }

    private boolean isSuperAdminConfigurationValid() {
        return StringUtils.hasText(superAdminEmail) && StringUtils.hasText(superAdminPassword);
    }

    private Role findSuperAdminRole() {
        return roleRepository.findByName(RoleEnum.SUPER_ADMIN)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: SUPER_ADMIN"));
    }

    private void createSuperAdmin(Role superAdminRole) {
        try {
            log.info("Creating super admin");
            RegisterUserDto superAdminDto = new RegisterUserDto();
            superAdminDto.setFirstName(SUPER_ADMIN_FIRST_NAME);
            superAdminDto.setLastName(SUPER_ADMIN_LAST_NAME);
            superAdminDto.setPhoneNumber(SUPER_ADMIN_PHONE);
            superAdminDto.setRole(RoleEnum.SUPER_ADMIN);
            superAdminDto.setEmail(superAdminEmail);
            superAdminDto.setPassword(superAdminPassword);

            authenticationService.signUp(superAdminDto);

            log.info("Super admin successfully created with email: {}", superAdminEmail);
        } catch (Exception e) {
            log.error("Failed to create super admin: {}", e.getMessage(), e);
        }
    }
}

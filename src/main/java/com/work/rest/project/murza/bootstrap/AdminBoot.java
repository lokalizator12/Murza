package com.work.rest.project.murza.bootstrap;

import com.work.rest.project.murza.dto.RegisterUserDto;
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

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        createSuperAdministrator();
    }

    private void createSuperAdministrator() {
        log.info("Searching for super admin");

        Role superAdminRole = roleRepository.findByName(RoleEnum.SUPER_ADMIN)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: SUPER_ADMIN"));

        if (!userRepository.existsByRole(superAdminRole)) {
            log.info("Initializing super admin");

            RegisterUserDto userDto = new RegisterUserDto();
            userDto.setFirstName("Super");
            userDto.setLastName("Admin");
            userDto.setPhoneNumber("0000000000");
            userDto.setRole(RoleEnum.SUPER_ADMIN);
            userDto.setEmail(superAdminEmail);
            userDto.setPassword(superAdminPassword);

            authenticationService.signUp(userDto);
            log.info("Super admin created with email: {}", superAdminEmail);
        } else {
            log.info("Super admin already exists");
        }
    }
}

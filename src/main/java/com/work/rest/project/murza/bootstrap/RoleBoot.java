package com.work.rest.project.murza.bootstrap;

import com.work.rest.project.murza.entity.Role;
import com.work.rest.project.murza.entity.RoleEnum;
import com.work.rest.project.murza.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleBoot implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleRepository roleRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initializeRoles();
    }

    private void initializeRoles() {
        log.info("Starting role initialization");

        Arrays.stream(RoleEnum.values()).forEach(roleEnum -> {
            roleRepository.findByName(roleEnum).ifPresentOrElse(
                    existingRole -> log.debug("Role '{}' already exists", existingRole.getName()),
                    () -> createRole(roleEnum)
            );
        });

        log.info("Role initialization complete");
    }

    private void createRole(RoleEnum roleEnum) {
        Role role = new Role();
        role.setName(roleEnum);
        role.setDescription(roleEnum.getDescription());
        roleRepository.save(role);
        log.info("Created role '{}' with description '{}'", roleEnum, roleEnum.getDescription());
    }
}

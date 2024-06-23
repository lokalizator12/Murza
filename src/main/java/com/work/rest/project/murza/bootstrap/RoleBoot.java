package com.work.rest.project.murza.bootstrap;

import com.work.rest.project.murza.entity.Role;
import com.work.rest.project.murza.entity.RoleEnum;
import com.work.rest.project.murza.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleBoot implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleRepository roleRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.loadRoles();
    }

    public void loadRoles() {
        log.info("Checking roles");

        if (roleRepository.count() != RoleEnum.values().length) {
            EnumSet<RoleEnum> roleEnums = EnumSet.of(RoleEnum.USER, RoleEnum.ADMIN, RoleEnum.SUPER_ADMIN);
            Map<RoleEnum, String> roleDescription = Map.of(
                    RoleEnum.USER, "Default user role (client)",
                    RoleEnum.ADMIN, "Default administrator role (moderator/manager)",
                    RoleEnum.SUPER_ADMIN, "Super-admin role (CEO | main-developer)"
            );

            log.info("Initializing roles");
            roleEnums.forEach(roleName -> {
                Optional<Role> role = roleRepository.findByName(roleName);
                role.ifPresentOrElse(
                        existingRole -> log.info("Role {} already exists with description: {}", existingRole.getName(), existingRole.getDescription()),
                        () -> {
                            Role roleForCreate = new Role();
                            roleForCreate.setName(roleName);
                            roleForCreate.setDescription(roleDescription.get(roleName));
                            roleRepository.save(roleForCreate);
                            log.info("Created role {} with description: {}", roleName, roleDescription.get(roleName));
                        }
                );
            });
        } else {
            log.info("All roles are already initialized");
        }
    }
}


package com.bank.asset_management.service.impl;

import com.bank.asset_management.entity.Permission;
import com.bank.asset_management.entity.Role;
import com.bank.asset_management.entity.User;
import com.bank.asset_management.repository.PermissionRepository;
import com.bank.asset_management.repository.RoleRepository;
import com.bank.asset_management.repository.UserRepository;
import com.bank.asset_management.util.constant.PermissionEnum;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    PasswordEncoder passwordEncoder;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_DEFAULT_PASSWORD = "Admin@123";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("[UDS] Load user by username={}", username);
        User user = userRepository.findByUsernameWithRolesAndPermissions(username).orElse(null);

        if (user == null) {
            log.warn("[UDS] Không tìm thấy user username={}", username);
            if (ADMIN_USERNAME.equalsIgnoreCase(username)) {
                log.warn("[UDS] Auto-create admin DEV username={} password={}", ADMIN_USERNAME, ADMIN_DEFAULT_PASSWORD);
                user = autoCreateAdmin();
            } else {
                throw new UsernameNotFoundException("Không tìm thấy người dùng: " + username);
            }
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            log.warn("[UDS] User username={} không có roles được gán", username);
        }

        Set<GrantedAuthority> authorities = user.getRoles() != null
                ? user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(perm -> new SimpleGrantedAuthority(perm.getName()))
                .collect(Collectors.toSet())
                : Set.of();

        log.debug("[UDS] User username={} roles={} permissions={}", username,
                user.getRoles() != null ? user.getRoles().size() : 0,
                authorities.size());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                authorities
        );
    }

    private User autoCreateAdmin() {
        // Tạo/gán role ADMIN
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Administrator role (auto-created)");
            adminRole.setPermissions(new HashSet<>());
            adminRole = roleRepository.save(adminRole);
            log.info("[UDS] ĐÃ TẠO role ADMIN");
        }
        if (adminRole.getPermissions() == null) adminRole.setPermissions(new HashSet<>());
        if (adminRole.getPermissions().isEmpty()) {
            for (PermissionEnum p : PermissionEnum.values()) {
                Permission perm = permissionRepository.findByName(p.name()).orElse(null);
                if (perm == null) {
                    perm = new Permission();
                    perm.setName(p.name());
                    perm.setDescription("Auto bootstrap: " + p.name());
                    perm = permissionRepository.save(perm);
                }
                adminRole.getPermissions().add(perm);
            }
            roleRepository.save(adminRole);
            log.info("[UDS] GÁN {} permissions cho role ADMIN", adminRole.getPermissions().size());
        }

        User admin = new User();
        admin.setUsername(ADMIN_USERNAME);
        admin.setPassword(passwordEncoder.encode(ADMIN_DEFAULT_PASSWORD));
        admin.setFullName("System Administrator");
        admin.setEmail("admin@example.com");
        admin.setActive(true);
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        admin.setRoles(roles);
        admin = userRepository.save(admin);
        log.info("[UDS] Auto-created admin user username={} password={} (bcrypt)", ADMIN_USERNAME, ADMIN_DEFAULT_PASSWORD);
        return admin;
    }
}

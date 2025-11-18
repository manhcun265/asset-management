package com.bank.asset_management.config;

import com.bank.asset_management.entity.Department;
import com.bank.asset_management.entity.Permission;
import com.bank.asset_management.entity.Role;
import com.bank.asset_management.entity.User;
import com.bank.asset_management.repository.DepartmentRepository;
import com.bank.asset_management.repository.PermissionRepository;
import com.bank.asset_management.repository.RoleRepository;
import com.bank.asset_management.repository.UserRepository;
import com.bank.asset_management.util.constant.PermissionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapData implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("[BOOTSTRAP] Starting data initialization...");

        // 1. Sync permissions from enum
        syncPermissions();

        // 2. Create roles and assign permissions
        createRoles();

        // 3. Create default department
        createDefaultDepartment();

        // 4. Create admin user if not exists
        createAdminUser();

        log.info("[BOOTSTRAP] Data initialization completed!");
    }

    private void syncPermissions() {
        log.info("[BOOTSTRAP] Syncing permissions...");
        int count = 0;

        for (PermissionEnum permEnum : PermissionEnum.values()) {
            if (!permissionRepository.existsByName(permEnum.name())) {
                Permission permission = new Permission();
                permission.setName(permEnum.name());
                permission.setDescription("Auto-synced: " + permEnum.name());
                permissionRepository.save(permission);
                count++;
            }
        }

        log.info("[BOOTSTRAP] Synced {} permissions (Total: {})", count, PermissionEnum.values().length);
    }

    private void createRoles() {
        log.info("[BOOTSTRAP] Creating roles...");

        // ADMIN Role - all permissions
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Administrator với tất cả quyền");
        }

        // Assign all permissions to ADMIN
        Set<Permission> allPermissions = new HashSet<>();
        for (PermissionEnum permEnum : PermissionEnum.values()) {
            permissionRepository.findByName(permEnum.name()).ifPresent(allPermissions::add);
        }
        adminRole.setPermissions(allPermissions);
        roleRepository.save(adminRole);
        log.info("[BOOTSTRAP] Role ADMIN created/updated with {} permissions", allPermissions.size());

        // USER Role - view only permissions
        Role userRole = roleRepository.findByName("USER").orElse(null);
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("USER");
            userRole.setDescription("Người dùng thông thường chỉ có quyền xem");
        }

        Set<Permission> userPermissions = new HashSet<>();
        permissionRepository.findByName("VIEW_ASSET").ifPresent(userPermissions::add);
        permissionRepository.findByName("VIEW_USER").ifPresent(userPermissions::add);
        permissionRepository.findByName("VIEW_ASSIGNMENT").ifPresent(userPermissions::add);
        userRole.setPermissions(userPermissions);
        roleRepository.save(userRole);
        log.info("[BOOTSTRAP] Role USER created/updated with {} permissions", userPermissions.size());
    }

    private void createDefaultDepartment() {
        if (departmentRepository.count() == 0) {
            Department dept = new Department();
            dept.setName("IT");
            dept.setDescription("Phòng Công Nghệ Thông Tin");
            departmentRepository.save(dept);
            log.info("[BOOTSTRAP] Created default department: IT");
        } else {
            log.info("[BOOTSTRAP] Departments already exist, skipping...");
        }
    }

    private void createAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setFullName("System Administrator");
            admin.setEmail("admin@assetmanagement.com");
            admin.setActive(true);

            // Assign ADMIN role
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            // Assign to IT department
            Department itDept = departmentRepository.findByName("IT")
                    .orElse(departmentRepository.findAll().stream().findFirst().orElse(null));
            if (itDept != null) {
                admin.setDepartment(itDept);
            }

            userRepository.save(admin);
            log.info("[BOOTSTRAP] Created default admin user: admin / password=Admin@123");
        } else {
            log.info("[BOOTSTRAP] Admin user already exists, skipping...");
        }
    }
}


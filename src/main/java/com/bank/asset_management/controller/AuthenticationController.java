package com.bank.asset_management.controller;

import com.bank.asset_management.dto.request.ChangePasswordRequest;
import com.bank.asset_management.dto.request.LoginRequest;
import com.bank.asset_management.dto.request.UserRequest;
import com.bank.asset_management.dto.response.LoginResponse;
import com.bank.asset_management.dto.response.UserResponse;
import com.bank.asset_management.entity.User;
import com.bank.asset_management.entity.Role;
import com.bank.asset_management.repository.UserRepository;
import com.bank.asset_management.repository.RoleRepository;
import com.bank.asset_management.service.JwtService;
import com.bank.asset_management.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationController {

    AuthenticationManager authenticationManager;
    UserDetailsService userDetailsService;
    JwtService jwtService;
    UserRepository userRepository;
    UserService userService;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    private static final String ADMIN_USERNAME = "admin";

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("[AUTH] Đăng nhập - username={}", request.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            log.warn("[AUTH] Sai thông tin đăng nhập cho username={}", request.getUsername());
            throw ex;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();

        log.info("[AUTH] Đăng nhập thành công - userId={}, username={}", user.getId(), user.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        log.info("[AUTH] Đăng ký tài khoản - username={}, email={}", request.getUsername(), request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.handleCreateUser(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String masked = (authHeader != null && authHeader.length() > 20) ? authHeader.substring(0, 20) + "..." : authHeader;
        log.info("[AUTH] Đăng xuất - Authorization={}", masked);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtService.invalidateToken(token);
            log.info("[AUTH] Đã vô hiệu token");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                               Principal principal) {
        if (principal == null || principal.getName() == null) {
            log.warn("[AUTH] Đổi mật khẩu - chưa đăng nhập");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("[AUTH] Đổi mật khẩu - mật khẩu hiện tại không đúng - username={}", user.getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("[AUTH] Đổi mật khẩu thành công - username={}", user.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/reset")
    public ResponseEntity<String> resetAdmin(@RequestParam(defaultValue = "Admin@123") String newPassword) {
        User admin = userRepository.findByUsername(ADMIN_USERNAME).orElse(null);
        if (admin == null) {
            admin = new User();
            admin.setUsername(ADMIN_USERNAME);
            admin.setFullName("System Administrator");
            admin.setEmail("admin@example.com");
            admin.setActive(true);
        }
        admin.setPassword(passwordEncoder.encode(newPassword));
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        if (adminRole != null) {
            if (admin.getRoles() == null) admin.setRoles(new HashSet<>());
            admin.getRoles().add(adminRole);
        }
        User saved = userRepository.save(admin);
        log.warn("[DEV] Reset/Tạo admin username={} password={} roles={} (dev only)", ADMIN_USERNAME, newPassword,
                saved.getRoles() != null ? saved.getRoles().size() : 0);
        return ResponseEntity.ok("Admin password reset to: " + newPassword);
    }

    @GetMapping("/admin/info")
    public ResponseEntity<Object> adminInfo() {
        User admin = userRepository.findByUsername(ADMIN_USERNAME).orElse(null);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin user not found");
        }
        var info = new java.util.HashMap<String, Object>();
        info.put("username", admin.getUsername());
        info.put("roles", admin.getRoles().stream().map(Role::getName).toList());
        info.put("permissions", admin.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getName())
                .distinct()
                .toList());
        info.put("active", admin.isActive());
        return ResponseEntity.ok(info);
    }
}

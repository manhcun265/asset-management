package com.bank.asset_management.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Asset Management API")
                .version("1.0.0")
                .description("Hệ thống quản lý tài sản với đầy đủ tính năng:\n\n" +
                    "- Authentication & Authorization (JWT)\n" +
                    "- Quản lý tài sản (Assets)\n" +
                    "- Quản lý phòng ban (Departments)\n" +
                    "- Quản lý người dùng (Users)\n" +
                    "- Gán tài sản (Asset Assignments)\n" +
                    "- Phân quyền (Roles & Permissions)\n\n" +
                    "**Tài khoản mặc định:** admin / Admin@123")
                .contact(new Contact()
                    .name("Asset Management Development Team")
                    .email("dev@assetmanagement.com")
                    .url("https://assetmanagement.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local Development Server"),
                new Server()
                    .url("https://api.assetmanagement.com")
                    .description("Production Server")
            ))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Nhập JWT token (lấy từ endpoint POST /api/auth/login)\n\n" +
                        "Ví dụ: eyJhbGciOiJIUzI1NiJ9...")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }
}


# Asset Management System

Hệ thống quản lý tài sản được xây dựng bằng Spring Boot.

## 📚 Tài Liệu

### Giải Thích Các Controller
Để hiểu rõ về các file controller trong dự án, vui lòng xem các tài liệu sau:

1. **[DOCS_CONTROLLER_SUMMARY.md](./DOCS_CONTROLLER_SUMMARY.md)** - Tóm tắt dễ hiểu về các controller
   - Giới thiệu tổng quan về từng controller
   - Ví dụ sử dụng
   - Kiến trúc hệ thống
   - Luồng hoạt động thường gặp

2. **[CONTROLLER_DOCUMENTATION.md](./CONTROLLER_DOCUMENTATION.md)** - Tài liệu kỹ thuật chi tiết
   - Tất cả các endpoint của từng controller
   - Format request/response
   - Validation rules
   - Business logic

## 🎯 Các Controller Chính

1. **AuthenticationController** - Xác thực và quản lý JWT token
2. **AssetController** - Quản lý tài sản
3. **AssetTypeController** - Quản lý loại tài sản
4. **AssetAssignmentController** - Quản lý bàn giao tài sản
5. **UserController** - Quản lý người dùng
6. **DepartmentController** - Quản lý phòng ban
7. **RoleController** - Quản lý vai trò
8. **PermissionController** - Quản lý quyền hạn

## 🚀 Bắt Đầu

### Chạy Ứng Dụng
```bash
./mvnw spring-boot:run
```

### API Base URL
```
http://localhost:8080/api
```

## 📖 Đọc Thêm

Xem [DOCS_CONTROLLER_SUMMARY.md](./DOCS_CONTROLLER_SUMMARY.md) để bắt đầu tìm hiểu về các controller trong dự án.

# Tóm Tắt Giải Thích Các Controller

## Hệ Thống Quản Lý Tài Sản (Asset Management System)

### Mục Đích
Hệ thống này được xây dựng bằng Spring Boot để quản lý tài sản, nhân sự và phân quyền trong một tổ chức.

---

## Danh Sách Các Controller

### 1. 🔐 AuthenticationController (`/api/auth`)
**Mục đích:** Xác thực và quản lý phiên đăng nhập

**Các chức năng chính:**
- Đăng nhập (login) và tạo JWT token
- Đăng ký tài khoản mới (register)
- Đăng xuất (logout) và hủy token
- Đổi mật khẩu (change-password)
- Reset tài khoản admin (cho dev)

**Ví dụ sử dụng:**
```
POST /api/auth/login
Body: { "username": "admin", "password": "Admin@123" }
→ Nhận JWT token để dùng cho các request sau
```

---

### 2. 📦 AssetController (`/api/assets`)
**Mục đích:** Quản lý các tài sản vật chất

**Các chức năng chính:**
- Tạo tài sản mới với mã tài sản duy nhất
- Xem danh sách tất cả tài sản (có phân trang)
- Xem chi tiết một tài sản
- Cập nhật thông tin tài sản
- Xóa tài sản

**Validation:**
- Mã tài sản không được trùng
- Loại tài sản phải tồn tại
- Phòng ban phải tồn tại

---

### 3. 🏷️ AssetTypeController (`/api/asset-types`)
**Mục đích:** Quản lý danh mục loại tài sản

**Các chức năng chính:**
- Tạo loại tài sản mới (VD: Laptop, Bàn ghế, Điều hòa)
- Xem danh sách các loại tài sản
- Cập nhật thông tin loại tài sản
- Xóa loại tài sản

**Validation:**
- Tên loại tài sản không được trùng (không phân biệt hoa thường)

---

### 4. 🤝 AssetAssignmentController (`/api/asset-assignments`)
**Mục đích:** Quản lý việc bàn giao tài sản cho nhân viên

**Các chức năng chính:**
- Tạo phiếu bàn giao tài sản
- Xem lịch sử bàn giao
- Cập nhật thông tin bàn giao (ngày trả, trạng thái)
- Xóa phiếu bàn giao

**Validation:**
- Tài sản phải tồn tại
- Người nhận phải tồn tại
- Ngày trả không được sớm hơn ngày bàn giao

**Ví dụ:** Bàn giao laptop cho nhân viên mới, ghi nhận ngày giao và ngày dự kiến trả

---

### 5. 👤 UserController (`/api/users`)
**Mục đích:** Quản lý người dùng trong hệ thống

**Các chức năng chính:**
- Tạo người dùng mới
- Xem danh sách người dùng
- Xem thông tin chi tiết người dùng
- Cập nhật thông tin người dùng
- Xóa người dùng

**Validation:**
- Username không được trùng
- Email không được trùng
- Phòng ban phải tồn tại

---

### 6. 🏢 DepartmentController (`/api/departments`)
**Mục đích:** Quản lý các phòng ban

**Các chức năng chính:**
- Tạo phòng ban mới (VD: IT, Kế toán, Nhân sự)
- Xem danh sách phòng ban
- Cập nhật thông tin phòng ban
- Xóa phòng ban

**Liên kết:** Phòng ban được liên kết với User và Asset

---

### 7. 👥 RoleController (`/api/roles`)
**Mục đích:** Quản lý vai trò trong hệ thống phân quyền

**Các chức năng chính:**
- Tạo vai trò mới (VD: ADMIN, MANAGER, EMPLOYEE)
- Xem danh sách vai trò
- Cập nhật vai trò
- Xóa vai trò

**Validation:**
- Tên vai trò không được trùng

**Mối quan hệ:**
- Một User có thể có nhiều Role
- Một Role có thể có nhiều Permission

---

### 8. 🔑 PermissionController (`/api/permissions`)
**Mục đích:** Quản lý quyền hạn chi tiết

**Các chức năng chính:**
- Tạo quyền mới (VD: CREATE_ASSET, DELETE_USER, VIEW_REPORT)
- Xem danh sách quyền
- Cập nhật quyền
- Xóa quyền

**Validation:**
- Tên quyền không được trùng

**Mối quan hệ:**
- Permission được gán cho Role
- User có quyền thông qua Role

---

## Kiến Trúc Chung

### Pattern
Tất cả controllers đều tuân theo pattern **RESTful CRUD**:
- **C**reate: POST
- **R**ead: GET (single + list)
- **U**pdate: PUT
- **D**elete: DELETE

### Cấu trúc 3 lớp
```
Controller (API Layer)
    ↓
Service (Business Logic Layer)
    ↓
Repository (Data Access Layer)
    ↓
Database
```

### Response Format
Mỗi endpoint trả về:
- **HTTP Status Code** (200, 201, 400, 404...)
- **Message** (thông báo tiếng Việt)
- **Data** (dữ liệu JSON)

### Phân trang
Các endpoint LIST hỗ trợ phân trang:
- Tham số: `page`, `size`, `sort`
- Mặc định: 20 items/page
- Ví dụ: `/api/assets?page=0&size=10&sort=createdAt,desc`

### Xác thực
- Sử dụng JWT token
- Token nhận từ `/api/auth/login`
- Gửi token qua header: `Authorization: Bearer <token>`

---

## Luồng Hoạt Động Tiêu Biểu

### 1. Đăng nhập
```
POST /api/auth/login
→ Nhận JWT token
→ Sử dụng token cho các request tiếp theo
```

### 2. Tạo tài sản mới
```
1. Đảm bảo loại tài sản tồn tại (GET /api/asset-types)
2. Đảm bảo phòng ban tồn tại (GET /api/departments)
3. Tạo tài sản (POST /api/assets)
```

### 3. Bàn giao tài sản
```
1. Chọn tài sản (GET /api/assets)
2. Chọn người nhận (GET /api/users)
3. Tạo phiếu bàn giao (POST /api/asset-assignments)
```

### 4. Quản lý phân quyền
```
1. Tạo permissions (POST /api/permissions)
2. Tạo role và gán permissions (POST /api/roles)
3. Tạo user và gán role (POST /api/users)
```

---

## Xử Lý Lỗi

### IdInvalidException
Sử dụng cho các lỗi business logic:
- ID không tồn tại
- Dữ liệu trùng lặp
- Logic không hợp lệ

### IllegalArgumentException
Sử dụng cho các lỗi validation cơ bản

### Ví dụ response lỗi:
```json
{
  "error": "Mã tài sản đã tồn tại! Vui lòng chọn mã khác.",
  "status": 400
}
```

---

## Tài Liệu Chi Tiết

Xem file `CONTROLLER_DOCUMENTATION.md` để biết thêm chi tiết về:
- Tất cả endpoints của từng controller
- Request/Response format
- Validation rules
- Business logic

---

## Tóm Tắt Theo Chức Năng

### Quản lý dữ liệu cơ bản
- Department (phòng ban)
- AssetType (loại tài sản)
- Permission (quyền)
- Role (vai trò)

### Quản lý người dùng
- User (người dùng)
- Authentication (xác thực)

### Quản lý tài sản
- Asset (tài sản)
- AssetAssignment (bàn giao)

### Mối quan hệ
```
User ← belongs to → Department
User ← has many → Role ← has many → Permission
Asset ← belongs to → AssetType
Asset ← belongs to → Department
AssetAssignment ← links → Asset + User
```

---

**Lưu ý:** Tất cả các message và exception đều được viết bằng tiếng Việt để dễ hiểu và sử dụng.

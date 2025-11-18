# Tài Liệu Giải Thích Các File Controller

## Tổng Quan
Dự án Asset Management sử dụng kiến trúc Spring Boot với 8 controller chính, xử lý các chức năng quản lý tài sản, người dùng, phân quyền và xác thực.

---

## 1. AuthenticationController
**Đường dẫn:** `/api/auth`

**Chức năng:** Xử lý các tác vụ liên quan đến xác thực và phân quyền người dùng.

### Các Endpoint:

#### POST `/api/auth/login`
- **Mô tả:** Đăng nhập vào hệ thống
- **Request Body:** LoginRequest (username, password)
- **Response:** LoginResponse chứa JWT token, thông tin user và roles
- **Chức năng:**
  - Xác thực username và password
  - Tạo JWT token cho phiên làm việc
  - Trả về thông tin user và danh sách vai trò

#### POST `/api/auth/register`
- **Mô tả:** Đăng ký tài khoản mới
- **Request Body:** UserRequest
- **Response:** UserResponse
- **Chức năng:** Tạo tài khoản người dùng mới trong hệ thống

#### POST `/api/auth/logout`
- **Mô tả:** Đăng xuất khỏi hệ thống
- **Header:** Authorization (Bearer token)
- **Chức năng:** Vô hiệu hóa JWT token hiện tại

#### POST `/api/auth/change-password`
- **Mô tả:** Đổi mật khẩu
- **Request Body:** ChangePasswordRequest (currentPassword, newPassword)
- **Yêu cầu:** User phải đăng nhập
- **Chức năng:**
  - Xác thực mật khẩu hiện tại
  - Cập nhật mật khẩu mới

#### POST `/api/auth/admin/reset`
- **Mô tả:** Reset hoặc tạo tài khoản admin (chỉ dùng cho development)
- **Query Param:** newPassword (mặc định: Admin@123)
- **Chức năng:** Tạo/reset mật khẩu cho tài khoản admin

#### GET `/api/auth/admin/info`
- **Mô tả:** Xem thông tin tài khoản admin
- **Response:** Thông tin admin bao gồm username, roles, permissions

---

## 2. AssetController
**Đường dẫn:** `/api/assets`

**Chức năng:** Quản lý tài sản của tổ chức.

### Các Endpoint:

#### POST `/api/assets`
- **Mô tả:** Tạo tài sản mới
- **Message:** "Tạo tài sản mới thành công!"
- **Request Body:** AssetRequest
- **Kiểm tra:**
  - Mã tài sản không được trùng lặp
  - Loại tài sản phải tồn tại
  - Phòng ban phải tồn tại
- **Response:** AssetResponse với HTTP 201 Created

#### PUT `/api/assets/{id}`
- **Mô tả:** Cập nhật thông tin tài sản
- **Message:** "Cập nhật thông tin tài sản thành công!"
- **Path Variable:** id - ID của tài sản
- **Request Body:** AssetRequest
- **Kiểm tra:**
  - Tài sản phải tồn tại
  - Mã tài sản mới không được trùng với tài sản khác
  - Loại tài sản và phòng ban phải hợp lệ

#### GET `/api/assets/{id}`
- **Mô tả:** Xem chi tiết một tài sản
- **Message:** "Hiển thị thông tin chi tiết tài sản thành công!"
- **Path Variable:** id - ID của tài sản
- **Response:** AssetResponse với đầy đủ thông tin

#### GET `/api/assets`
- **Mô tả:** Xem danh sách tất cả tài sản
- **Message:** "Hiển thị danh sách tài sản thành công!"
- **Query Params:** Pageable (size mặc định: 20)
- **Response:** Page<AssetResponse> - danh sách phân trang

#### DELETE `/api/assets/{id}`
- **Mô tả:** Xóa tài sản
- **Message:** "Xóa tài sản thành công!"
- **Path Variable:** id - ID của tài sản
- **Kiểm tra:** Tài sản phải tồn tại

---

## 3. AssetTypeController
**Đường dẫn:** `/api/asset-types`

**Chức năng:** Quản lý các loại tài sản (máy tính, bàn ghế, thiết bị văn phòng...).

### Các Endpoint:

#### POST `/api/asset-types`
- **Mô tả:** Tạo loại tài sản mới
- **Message:** "Tạo loại tài sản mới thành công!"
- **Request Body:** AssetTypeRequest
- **Kiểm tra:** Tên loại tài sản không được trùng lặp (case-insensitive)

#### PUT `/api/asset-types/{id}`
- **Mô tả:** Cập nhật thông tin loại tài sản
- **Message:** "Cập nhật thông tin loại tài sản thành công!"
- **Kiểm tra:**
  - Loại tài sản phải tồn tại
  - Tên mới không được trùng với loại tài sản khác

#### GET `/api/asset-types/{id}`
- **Mô tả:** Xem chi tiết loại tài sản
- **Message:** "Hiển thị thông tin chi tiết loại tài sản thành công!"

#### GET `/api/asset-types`
- **Mô tả:** Xem danh sách loại tài sản
- **Message:** "Hiển thị danh sách loại tài sản thành công!"
- **Query Params:** Pageable (size mặc định: 20)

#### DELETE `/api/asset-types/{id}`
- **Mô tả:** Xóa loại tài sản
- **Message:** "Xóa loại tài sản thành công!"

---

## 4. AssetAssignmentController
**Đường dẫn:** `/api/asset-assignments`

**Chức năng:** Quản lý việc bàn giao tài sản cho nhân viên.

### Các Endpoint:

#### POST `/api/asset-assignments`
- **Mô tả:** Tạo phiếu bàn giao tài sản mới
- **Message:** "Tạo bàn giao tài sản mới thành công!"
- **Request Body:** AssetAssignmentRequest
- **Kiểm tra:**
  - Tài sản phải tồn tại
  - Người dùng phải tồn tại
  - Ngày trả không được sớm hơn ngày bàn giao

#### PUT `/api/asset-assignments/{id}`
- **Mô tả:** Cập nhật thông tin bàn giao
- **Message:** "Cập nhật thông tin bàn giao tài sản thành công!"
- **Kiểm tra:**
  - Phiếu bàn giao phải tồn tại
  - Tài sản và người dùng phải hợp lệ
  - Logic ngày tháng hợp lý

#### GET `/api/asset-assignments/{id}`
- **Mô tả:** Xem chi tiết phiếu bàn giao
- **Message:** "Hiển thị thông tin chi tiết bàn giao tài sản thành công!"

#### GET `/api/asset-assignments`
- **Mô tả:** Xem danh sách bàn giao
- **Message:** "Hiển thị danh sách bàn giao tài sản thành công!"
- **Query Params:** Pageable (size mặc định: 20)

#### DELETE `/api/asset-assignments/{id}`
- **Mô tả:** Xóa phiếu bàn giao
- **Message:** "Xóa bàn giao tài sản thành công!"

---

## 5. UserController
**Đường dẫn:** `/api/users`

**Chức năng:** Quản lý người dùng trong hệ thống.

### Các Endpoint:

#### POST `/api/users`
- **Mô tả:** Tạo người dùng mới
- **Message:** "Tạo người dùng mới thành công!"
- **Request Body:** UserRequest
- **Kiểm tra:**
  - Username không được trùng lặp
  - Email không được trùng lặp
  - Phòng ban phải tồn tại

#### PUT `/api/users/{id}`
- **Mô tả:** Cập nhật thông tin người dùng
- **Message:** "Cập nhật thông tin người dùng thành công!"
- **Kiểm tra:**
  - Người dùng phải tồn tại
  - Username mới không được trùng với user khác
  - Email mới không được trùng với user khác
  - Phòng ban phải hợp lệ

#### GET `/api/users/{id}`
- **Mô tả:** Xem chi tiết người dùng
- **Message:** "Hiển thị thông tin chi tiết người dùng thành công!"

#### GET `/api/users`
- **Mô tả:** Xem danh sách người dùng
- **Message:** "Hiển thị danh sách người dùng thành công!"
- **Query Params:** Pageable (size mặc định: 20)

#### DELETE `/api/users/{id}`
- **Mô tả:** Xóa người dùng
- **Message:** "Xóa người dùng thành công!"

---

## 6. DepartmentController
**Đường dẫn:** `/api/departments`

**Chức năng:** Quản lý các phòng ban trong tổ chức.

### Các Endpoint:

#### POST `/api/departments`
- **Mô tả:** Tạo phòng ban mới
- **Message:** "Tạo phòng ban mới thành công!"
- **Request Body:** DepartmentRequest

#### PUT `/api/departments/{id}`
- **Mô tả:** Cập nhật thông tin phòng ban
- **Message:** "Cập nhật thông tin phòng ban thành công!"
- **Kiểm tra:** Phòng ban phải tồn tại

#### GET `/api/departments/{id}`
- **Mô tả:** Xem chi tiết phòng ban
- **Message:** "Hiển thị thông tin chi tiết phòng ban thành công!"

#### GET `/api/departments`
- **Mô tả:** Xem danh sách phòng ban
- **Message:** "Hiển thị danh sách phòng ban thành công!"
- **Query Params:** Pageable (size mặc định: 20)

#### DELETE `/api/departments/{id}`
- **Mô tả:** Xóa phòng ban
- **Message:** "Xóa phòng ban thành công!"

---

## 7. RoleController
**Đường dẫn:** `/api/roles`

**Chức năng:** Quản lý các vai trò (roles) trong hệ thống phân quyền.

### Các Endpoint:

#### POST `/api/roles`
- **Mô tả:** Tạo vai trò mới
- **Message:** "Tạo vai trò mới thành công!"
- **Request Body:** RoleRequest
- **Kiểm tra:** Tên vai trò không được trùng lặp

#### PUT `/api/roles/{id}`
- **Mô tả:** Cập nhật thông tin vai trò
- **Message:** "Cập nhật thông tin vai trò thành công!"
- **Kiểm tra:**
  - Vai trò phải tồn tại
  - Tên mới không được trùng với vai trò khác

#### GET `/api/roles/{id}`
- **Mô tả:** Xem chi tiết vai trò
- **Message:** "Hiển thị thông tin chi tiết vai trò thành công!"

#### GET `/api/roles`
- **Mô tả:** Xem danh sách vai trò
- **Message:** "Hiển thị danh sách vai trò thành công!"
- **Query Params:** Pageable (size mặc định: 20)

#### DELETE `/api/roles/{id}`
- **Mô tả:** Xóa vai trò
- **Message:** "Xóa vai trò thành công!"

---

## 8. PermissionController
**Đường dẫn:** `/api/permissions`

**Chức năng:** Quản lý các quyền (permissions) trong hệ thống phân quyền.

### Các Endpoint:

#### POST `/api/permissions`
- **Mô tả:** Tạo quyền mới
- **Message:** "Tạo quyền mới thành công!"
- **Request Body:** PermissionRequest
- **Kiểm tra:** Tên quyền không được trùng lặp

#### PUT `/api/permissions/{id}`
- **Mô tả:** Cập nhật thông tin quyền
- **Message:** "Cập nhật thông tin quyền thành công!"
- **Kiểm tra:**
  - Quyền phải tồn tại
  - Tên mới không được trùng với quyền khác

#### GET `/api/permissions/{id}`
- **Mô tả:** Xem chi tiết quyền
- **Message:** "Hiển thị thông tin chi tiết quyền thành công!"

#### GET `/api/permissions`
- **Mô tả:** Xem danh sách quyền
- **Message:** "Hiển thị danh sách quyền thành công!"
- **Query Params:** Pageable (size mặc định: 20)

#### DELETE `/api/permissions/{id}`
- **Mô tả:** Xóa quyền
- **Message:** "Xóa quyền thành công!"

---

## Các Annotation Chung

### @RestController
Đánh dấu class là REST controller, tự động serialize response thành JSON.

### @RequestMapping
Định nghĩa base path cho tất cả endpoints trong controller.

### @RequiredArgsConstructor
Lombok annotation tự động tạo constructor với các field final.

### @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
Lombok annotation đặt tất cả fields là private và final.

### @ApiMessage
Custom annotation để thêm message tùy chỉnh vào response.

### @Valid
Kích hoạt validation cho request body.

## Pattern Chung

Tất cả controllers đều tuân theo pattern CRUD chuẩn:
1. **Create (POST):** Tạo mới resource với validation
2. **Read (GET):** Lấy thông tin single hoặc list (có phân trang)
3. **Update (PUT):** Cập nhật resource với validation
4. **Delete (DELETE):** Xóa resource

## Xử Lý Exception

Controllers sử dụng 2 loại exception chính:
- **IdInvalidException:** Cho các lỗi liên quan đến business logic
- **IllegalArgumentException:** Cho các lỗi validation cơ bản

## Dependency Injection

Tất cả controllers sử dụng:
- **Service layer:** Xử lý business logic
- **Repository layer:** Truy cập database
- Injection thông qua constructor (RequiredArgsConstructor)

## Pagination

Các endpoint LIST đều hỗ trợ phân trang:
- Size mặc định: 20 items/page
- Có thể tùy chỉnh thông qua query parameters
- Return type: `Page<T>` từ Spring Data

## Security

- AuthenticationController xử lý đăng nhập/đăng ký
- Sử dụng JWT token cho authentication
- Token được truyền qua Authorization header (Bearer token)
- Hỗ trợ logout bằng cách invalidate token

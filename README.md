# Hệ Thống Quản Lý Video - Baitaptuan6

Dự án Spring Boot thực hiện chức năng CRUD và tìm kiếm phân trang cho Category, User và Video với quyền admin.

## Công Nghệ Sử Dụng

- **Backend**: Spring Boot 3.5.6, Spring Data JPA, Spring Security
- **Frontend**: Thymeleaf, Thymeleaf Layout Dialect, Bootstrap 5
- **Database**: PostgreSQL 15 (Docker)
- **Build Tool**: Maven
- **Java**: 17

## Cài Đặt và Chạy Dự Án

### 1. Yêu Cầu Hệ Thống

- Java 17+
- Maven 3.6+
- Docker và Docker Compose

### 2. Chạy Database

```bash
# Chạy PostgreSQL bằng Docker Compose
docker-compose up -d

# Kiểm tra container đang chạy
docker ps
```

### 3. Chạy Ứng Dụng

```bash
# Build và chạy ứng dụng
mvn spring-boot:run

# Hoặc build JAR file
mvn clean package
java -jar target/baitaptuan6-0.0.1-SNAPSHOT.jar
```

### 4. Truy Cập Ứng Dụng

- URL: http://localhost:8080
- Database: PostgreSQL trên port 5432

## Cấu Trúc Dự Án

### Entities
- **Category**: Quản lý danh mục video
- **User**: Quản lý người dùng (Admin/User)
- **Video**: Quản lý video với liên kết đến Category và User

### Tính Năng Chính

#### Category Management
- CRUD operations (Create, Read, Update, Delete)
- Tìm kiếm theo tên và mô tả
- Phân trang và sắp xếp
- Soft delete (vô hiệu hóa/kích hoạt)

#### User Management
- CRUD operations
- Phân quyền Admin/User
- Tìm kiếm theo username, email, họ tên
- Mã hóa mật khẩu với BCrypt

#### Video Management
- CRUD operations
- Liên kết với Category và User
- Tìm kiếm theo tiêu đề và mô tả
- Thống kê lượt xem và lượt thích
- Phân trang và sắp xếp

## API Endpoints

### Category
- `GET /admin/categories` - Danh sách categories
- `GET /admin/categories/new` - Form tạo mới
- `POST /admin/categories` - Tạo category
- `GET /admin/categories/{id}/edit` - Form chỉnh sửa
- `PUT /admin/categories/{id}` - Cập nhật category
- `DELETE /admin/categories/{id}` - Xóa category
- `PATCH /admin/categories/{id}/toggle` - Bật/tắt category

### User
- `GET /admin/users` - Danh sách users
- `GET /admin/users/new` - Form tạo mới
- `POST /admin/users` - Tạo user
- `GET /admin/users/{id}/edit` - Form chỉnh sửa
- `PUT /admin/users/{id}` - Cập nhật user
- `DELETE /admin/users/{id}` - Xóa user
- `PATCH /admin/users/{id}/toggle` - Bật/tắt user

### Video
- `GET /admin/videos` - Danh sách videos
- `GET /admin/videos/new` - Form tạo mới
- `POST /admin/videos` - Tạo video
- `GET /admin/videos/{id}/edit` - Form chỉnh sửa
- `PUT /admin/videos/{id}` - Cập nhật video
- `DELETE /admin/videos/{id}` - Xóa video
- `PATCH /admin/videos/{id}/toggle` - Bật/tắt video

## Cấu Hình Database

Database được cấu hình trong `application.properties`:
- Host: localhost:5432
- Database: baitaptuan6
- Username: postgres
- Password: postgres

## Tác Giả

- **Tên**: Phan Quốc Viễn
- **MSSV**: 23110362
- **GitHub**: Viendeptrai1

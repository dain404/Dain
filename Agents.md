# AGENTS.md – Luật bắt buộc cho AI Agent

> ⚠️ Đọc toàn bộ file này trước khi tạo, sửa, hay xóa bất kỳ file nào trong project.

---

## 1. TỔNG QUAN DỰ ÁN

**Tên dự án:** Hệ thống quản lý và bán bánh ngọt trực tuyến (Online Sweet Cake Shop)  
**Giao diện tham khảo:** Tous les Jours (bakery style – tone màu trắng + beige ấm #c8a96e)  
**Ngôn ngữ:** Tiếng Việt toàn bộ UI, comment code bằng tiếng Việt

---

## 2. TECH STACK – KHÔNG ĐƯỢC THAY ĐỔI

| Thành phần     | Công nghệ bắt buộc                         |
|---------------|---------------------------------------------|
| Language       | Java JDK 21                                |
| Framework      | Spring Boot 3.x                            |
| View Engine    | Thymeleaf + Bootstrap 5 + FontAwesome      |
| Security       | Spring Security (BCrypt, ROLE_USER/ROLE_ADMIN) |
| Database       | MS SQL Server 2019+ (KHÔNG dùng MySQL/H2)  |
| ORM            | Spring Data JPA + Hibernate                |
| Nâng cao       | WebSocket (STOMP), @Async, JavaMailSender  |
| Build tool     | Maven (pom.xml đã có, KHÔNG đổi sang Gradle) |
| IDE target     | IntelliJ IDEA / VS Code                    |

---

## 3. CẤU TRÚC THƯ MỤC – KHÔNG ĐƯỢC THAY ĐỔI

```
bakery_shop/
├── src/
│   ├── main/
│   │   ├── java/com/example/bakery_shop/
│   │   │   ├── config/          ← SecurityConfig, WebSocketConfig, AsyncConfig
│   │   │   ├── controller/      ← *Controller.java (KHÔNG để logic ở đây)
│   │   │   ├── service/         ← *Service.java + *ServiceImpl.java
│   │   │   ├── repository/      ← *Repository.java (extends JpaRepository)
│   │   │   ├── entity/          ← Entity classes mapping với DB
│   │   │   ├── dto/             ← Data Transfer Objects
│   │   │   └── BakeryShopApplication.java
│   │   └── resources/
│   │       ├── templates/       ← File .html Thymeleaf
│   │       │   ├── layout/      ← fragments: header.html, footer.html, navbar.html
│   │       │   ├── user/        ← trang dành cho khách hàng
│   │       │   └── admin/       ← trang dành cho admin
│   │       ├── static/
│   │       │   ├── css/         ← custom CSS (style.css)
│   │       │   ├── js/          ← custom JS
│   │       │   └── images/      ← ảnh tĩnh
│   │       └── application.properties
└── pom.xml
```

---

## 4. DATABASE – SQL SERVER (KHÔNG dùng MySQL/H2)

**Tên database:** `BanhNgotDB`  
**Connection string mẫu:**
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=BanhNgotDB;encrypt=false
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
spring.jpa.hibernate.ddl-auto=update
```

### Các bảng bắt buộc (đúng tên này):

| Entity Java     | Tên bảng SQL     |
|----------------|-----------------|
| NguoiDung      | NGUOIDUNG        |
| SanPham        | SANPHAM          |
| DanhMuc        | DANHMUC          |
| DonHang        | DONHANG          |
| ChiTietDonHang | CHITIET_DONHANG  |
| ThanhToan      | THANHTOAN        |
| GioHang        | GIO_HANG         |

---

## 5. CÁC ENTITY BẮT BUỘC

### NguoiDung
```java
@Entity @Table(name = "NGUOIDUNG")
// Fields: userId(PK), hoTen, email(UNIQUE), matKhau(BCrypt),
//         soDienThoai, diaChi, vaiTro(DEFAULT "ROLE_USER"), ngayTao
```

### SanPham
```java
@Entity @Table(name = "SANPHAM")
// Fields: sanPhamId(PK), danhMucId(FK), tenSanPham, moTa,
//         gia(DECIMAL), soLuongTon, hinhAnh(URL), trangThai(BIT)
```

### DonHang – Trạng thái enum:
```
CHO_XAC_NHAN → DA_XAC_NHAN → DANG_CHUAN_BI → DANG_GIAO → HOAN_THANH
                                                          → HUY
                                                          → HOAN_TIEN
```

---

## 6. LUẬT VIẾT CODE

### ✅ BẮT BUỘC:
- Dùng **@Service, @Repository, @Controller, @Entity** đúng layer
- Controller chỉ gọi Service, KHÔNG viết logic DB trong Controller
- Dùng **DTO** để truyền dữ liệu giữa Controller ↔ View
- Dùng **Thymeleaf fragments** cho header/footer/navbar (layout/header.html)
- Mỗi Thymeleaf template PHẢI có `xmlns:th="http://www.thymeleaf.org"`
- Dùng **@Transactional** ở Service layer khi có thao tác write DB
- Mật khẩu PHẢI mã hóa bằng **BCryptPasswordEncoder**
- Dùng **Lombok** (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor)

### ❌ KHÔNG được làm:
- Không viết SQL thuần (dùng JPA methods hoặc @Query với JPQL)
- Không để hardcode password/secret trong code (dùng application.properties)
- Không bỏ qua Spring Security cho các trang admin
- Không dùng JSP (chỉ dùng Thymeleaf .html)
- Không đổi tên bảng DB hay tên package

---

## 7. PHÂN QUYỀN (Spring Security)

```
ROLE_USER  → truy cập: /, /products/**, /cart/**, /orders/**, /profile/**
ROLE_ADMIN → truy cập: /admin/** (tất cả trang trên + admin dashboard)
Public     → truy cập: /login, /register, /products (xem), /static/**
```

---

## 8. CÁC TRANG PHẢI TẠO

### User (src/main/resources/templates/user/):
| File                | Chức năng                              |
|--------------------|-----------------------------------------|
| index.html         | Trang chủ – hero banner + sản phẩm nổi bật |
| products.html      | Danh sách sản phẩm + lọc theo danh mục |
| product-detail.html| Chi tiết sản phẩm + đánh giá          |
| cart.html          | Giỏ hàng                              |
| checkout.html      | Đặt hàng + chọn thanh toán            |
| order-history.html | Lịch sử đơn hàng                      |
| profile.html       | Thông tin tài khoản                    |

### Auth:
| File               | Chức năng   |
|-------------------|-------------|
| login.html        | Đăng nhập   |
| register.html     | Đăng ký     |

### Admin (src/main/resources/templates/admin/):
| File                    | Chức năng                |
|------------------------|--------------------------|
| dashboard.html         | Thống kê doanh thu        |
| products-manage.html   | Quản lý sản phẩm (CRUD)  |
| orders-manage.html     | Quản lý đơn hàng          |
| users-manage.html      | Quản lý người dùng        |

---

## 9. GIAO DIỆN (Style Guide)

- **Màu chính:** `#c8a96e` (beige ấm), trắng `#ffffff`, nền nhạt `#faf8f5`
- **Font heading:** serif (ví dụ Playfair Display hoặc Georgia)
- **Font body:** sans-serif (Inter hoặc Roboto)
- **Bootstrap version:** 5.3.x (CDN)
- **FontAwesome:** 6.x (CDN)
- Layout trang chủ theo thứ tự:
  1. Navbar (logo trái, menu giữa, cart/login phải)
  2. Hero Banner (ảnh full-width + overlay text)
  3. Section Sản phẩm nổi bật (grid 4 cột)
  4. Section Danh mục (grid icon + tên)
  5. Section Giới thiệu thương hiệu
  6. Footer (dark background, 4 cột links)

---

## 10. THỨ TỰ TẠO FILE (Agent phải theo đúng thứ tự này)

```
Bước 1: pom.xml (thêm dependencies còn thiếu)
Bước 2: application.properties (cấu hình SQL Server)
Bước 3: Entity classes (NguoiDung, DanhMuc, SanPham, DonHang, ChiTietDonHang, ThanhToan)
Bước 4: Repository interfaces
Bước 5: Service interfaces + ServiceImpl
Bước 6: Config (SecurityConfig, WebSocketConfig)
Bước 7: Controller classes
Bước 8: DTO classes
Bước 9: Thymeleaf templates (layout fragments → user pages → admin pages)
Bước 10: database-init.sql (tạo bảng + INSERT dữ liệu mẫu)
```

---

## 11. DỮ LIỆU MẪU CẦN CÓ

- 3 danh mục: Bánh kem, Bánh mì ngọt, Đồ uống
- 10 sản phẩm mẫu với ảnh URL từ Unsplash (bánh ngọt)
- 2 tài khoản: admin@bakery.com (ROLE_ADMIN) + user@bakery.com (ROLE_USER)
- Mật khẩu mẫu: `123456` (lưu dạng BCrypt hash trong SQL)

---

## 12. QUY TRÌNH BẮT BUỘC SAU MỖI BƯỚC

Agent PHẢI thực hiện đúng chu trình này trước khi qua bước tiếp:

### GATE CHECK (bắt buộc sau mỗi bước):

1. **BUILD CHECK**
   - Chạy: `./mvnw compile`
   - Nếu lỗi → tự sửa → chạy lại cho đến khi PASS
   - Không được bỏ qua bước này

2. **SELF REVIEW**
   Tự kiểm tra các mục sau và trả lời YES/NO:
   - [ ] Đúng package `com.example.bakery_shop`?
   - [ ] Đúng tên bảng SQL theo AGENTS.md mục 5?
   - [ ] Không có logic DB trong Controller?
   - [ ] Không hardcode password?
   - [ ] Dùng đúng SQL Server (không phải MySQL/H2)?

3. **BÁO CÁO** – Trả lời theo đúng format sau trước khi qua bước tiếp:
---

*File này được tạo tự động dựa trên báo cáo đồ án. Không xóa hay sửa file này.*
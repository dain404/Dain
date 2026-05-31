# 🍰 Dự án Quản lý Tiệm Bánh Ngọt (Bakery Shop)

Đây là dự án Web bán bánh ngọt trực tuyến được xây dựng bằng **Java Spring Boot**, **Thymeleaf**, và **MS SQL Server**.

## 🛠 Yêu cầu hệ thống
- **Java JDK**: 21
- **Cơ sở dữ liệu**: Microsoft SQL Server (2019 hoặc mới hơn)
- **IDE khuyên dùng**: IntelliJ IDEA, Eclipse, hoặc VS Code

---

## 🚀 Hướng dẫn cài đặt và chạy ứng dụng

### Bước 1: Chuẩn bị Cơ sở dữ liệu (Database)
1. Mở **SQL Server Management Studio (SSMS)**.
2. Đăng nhập vào SQL Server (chọn SQL Server Authentication hoặc Windows Authentication đều được).
3. Chạy lệnh sau để tạo database mới:
   ```sql
   CREATE DATABASE BanhNgotDB;
   ```

### Bước 2: Cấu hình kết nối Database
Mở file `src/main/resources/application.properties` và sửa lại thông tin cấu hình SQL Server cho phù hợp với máy của bạn.

> **Lưu ý**: Hiện tại ứng dụng đang được cấu hình với port động `53460` và username `sa`. **Bạn CẦN PHẢI sửa lại** phần này.

Ví dụ nếu bạn dùng mặc định (cổng 1433) và đăng nhập bằng Windows Authentication:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=BanhNgotDB;encrypt=false;trustServerCertificate=true;integratedSecurity=true
```
Hoặc nếu dùng SQL Server Authentication:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=BanhNgotDB;encrypt=false;trustServerCertificate=true
spring.datasource.username=TÊN_ĐĂNG_NHẬP_CỦA_BẠN (ví dụ: sa)
spring.datasource.password=MẬT_KHẨU_CỦA_BẠN
```

### Bước 3: Khởi động ứng dụng
Khởi động ứng dụng bằng IDE của bạn (chạy file `BakeryShopApplication.java`) hoặc dùng lệnh Terminal:
```bash
./mvnw spring-boot:run
```

> 🌟 **Điểm cộng đặc biệt**: Hệ thống đã được tích hợp cơ chế tự động nạp dữ liệu mẫu (`DataInitializer.java`). Ngay khi ứng dụng khởi chạy lần đầu tiên, các bảng cơ sở dữ liệu sẽ tự động được tạo ra và các danh mục, 10 sản phẩm mẫu (kèm ảnh), cùng với 2 tài khoản thử nghiệm (`admin@bakery.com`, `user@bakery.com`) sẽ tự động được chèn vào DB. Bạn **không cần phải chạy lệnh SQL thủ công nào**!

### Bước 4: Chèn dữ liệu thủ công (Không bắt buộc - Chỉ dùng làm dự phòng)
Nếu vì lý do nào đó bạn muốn đặt lại dữ liệu gốc từ đầu:
1. Mở **SQL Server Management Studio**.
2. Mở file `src/main/resources/database-init.sql`.
3. Bấm **Execute** (F5) để chạy thủ công dữ liệu mẫu vào database `BanhNgotDB`.

---

## 🌐 Trải nghiệm ứng dụng

Sau khi khởi động và nạp dữ liệu thành công, mở trình duyệt và truy cập:

- **Trang chủ**: [http://localhost:8080](http://localhost:8080)
- **Trang quản trị (Admin)**: [http://localhost:8080/admin](http://localhost:8080/admin)

### Tài khoản đăng nhập mẫu:
| Quyền | Email | Mật khẩu |
|-------|-------|----------|
| **Admin** | `admin@bakery.com` | `123456` |
| **Khách hàng** | `user@bakery.com` | `123456` |

---

## 📌 Khắc phục sự cố thường gặp
- **Lỗi Font chữ tiếng Việt trong SQL Server**: Toàn bộ Entity của dự án đã được cấu hình dùng kiểu dữ liệu `NVARCHAR` (`columnDefinition = "NVARCHAR(...)"`). Hãy chắc chắn bạn nạp dữ liệu bằng SSMS thay vì Terminal để tránh lỗi Encoding.
- **Lỗi không kết nối được Database (Login Failed)**: Hãy kiểm tra kỹ lại `application.properties`. Máy mỗi người có cấu hình SQL Server khác nhau (SQLEXPRESS vs MSSQLSERVER, port 1433 vs port động).

-- =========================================================
-- DATABASE INIT SCRIPT CHO DỰ ÁN BAKERY SHOP
-- Chạy script này trong SQL Server (BanhNgotDB) 
-- sau khi Spring Boot đã tự động tạo bảng (ddl-auto=update)
-- =========================================================

-- 1. Dữ liệu mẫu cho bảng NGUOIDUNG
-- Mật khẩu mặc định: 123456 
-- (Được mã hóa bằng BCrypt: $2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUawMiLMBQ1y)
INSERT INTO NGUOIDUNG (ho_ten, email, mat_khau, so_dien_thoai, dia_chi, vai_tro, ngay_tao)
VALUES 
(N'Admin Tiệm Bánh', 'admin@bakery.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUawMiLMBQ1y', '0901234567', N'123 Đường Bánh Ngọt, Quận 1, TP.HCM', 'ROLE_ADMIN', CURRENT_TIMESTAMP),
(N'Khách Hàng', 'user@bakery.com', '$2a$10$W2neF9.6Agi6kAKVq8q3fec5dHW8KUA.b0VSIGdIZyUawMiLMBQ1y', '0909876543', N'456 Đường Trà Sữa, Quận 3, TP.HCM', 'ROLE_USER', CURRENT_TIMESTAMP);

-- 2. Dữ liệu mẫu cho bảng DANHMUC
INSERT INTO DANHMUC (ten_danh_muc, mo_ta, icon)
VALUES 
(N'Bánh kem', N'Các loại bánh kem sinh nhật, bánh sự kiện', 'fa-solid fa-cake-candles'),
(N'Bánh mì ngọt', N'Bánh sừng bò, bánh mì ngọt mềm dùng hàng ngày', 'fa-solid fa-bread-slice'),
(N'Đồ uống', N'Cà phê, trà trái cây và các loại nước giải khát', 'fa-solid fa-mug-hot');

-- 3. Dữ liệu mẫu cho bảng SANPHAM
INSERT INTO SANPHAM (danh_muc_id, ten_san_pham, mo_ta, gia, so_luong_ton, hinh_anh, trang_thai)
VALUES 
-- Danh mục 1: Bánh kem (ID: 1)
(1, N'Bánh Kem Dâu Tây Thượng Hạng', N'Bánh kem sữa tươi nguyên chất phủ dâu tây tươi mọng nước từ Đà Lạt. Vị ngọt thanh không ngấy.', 350000, 20, '/images/products/banh-kem-dau-tay.jpg', 1),
(1, N'Bánh Chocolate Truffle', N'Bánh kem socola đậm vị, phủ bột cacao và socola bào nguyên chất Bỉ.', 420000, 15, '/images/products/banh-chocolate-truffle.jpg', 1),
(1, N'Bánh Tiramisu Ý', N'Sự kết hợp hoàn hảo giữa phô mai mascarpone béo ngậy, cà phê espresso và bột cacao nguyên chất.', 380000, 10, '/images/products/banh-tiramisu.jpg', 1),
(1, N'Bánh Mousse Trà Xanh', N'Lớp mousse mềm mịn hòa quyện cùng hương vị matcha thanh mát chuẩn Nhật Bản.', 290000, 25, '/images/products/banh-mousse-tra-xanh.jpg', 1),

-- Danh mục 2: Bánh mì ngọt (ID: 2)
(2, N'Bánh Croissant Bơ Pháp', N'Bánh sừng bò ngàn lớp thơm lừng bơ Pháp cao cấp. Phù hợp cho bữa sáng nhẹ nhàng.', 45000, 50, '/images/products/banh-croissant.jpg', 1),
(2, N'Bánh Macaron Assorted', N'Hộp 6 chiếc bánh macaron đủ vị truyền thống Pháp: Vanilla, Socola, Dâu tây, Chanh, Matcha, Cà phê.', 150000, 30, '/images/products/banh-macaron.jpg', 1),
(2, N'Bánh Muffin Việt Quất', N'Bánh nướng xốp mềm hòa quyện với trái việt quất tươi mọng nước.', 35000, 40, '/images/products/banh-muffin.jpg', 1),

-- Danh mục 3: Đồ uống (ID: 3)
(3, N'Trà Sữa Trân Châu Hoàng Gia', N'Trà đen hảo hạng kết hợp sữa tươi thanh trùng và trân châu đường đen dẻo dai.', 45000, 100, '/images/products/tra-sua-tran-chau.jpg', 1),
(3, N'Cà Phê Sữa Đá Sài Gòn', N'Cà phê rang xay đậm vị pha trộn cùng sữa đặc có đường theo chuẩn truyền thống Sài Gòn.', 35000, 100, '/images/products/ca-phe-sua-da.jpg', 1),
(3, N'Trà Đào Cam Sả', N'Thức uống giải nhiệt ngày hè với đào ngâm giòn ngọt, cam tươi vắt và sả thơm lừng.', 50000, 80, '/images/products/tra-dao-cam-sa.jpg', 1);
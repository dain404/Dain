# 📸 Hướng Dẫn Import Ảnh

## Đổi tên thương hiệu
Tìm và thay toàn bộ `[TEN_THUONG_HIEU]` bằng tên thật của bạn trong tất cả file HTML.
Tương tự với `[email]` trong footer và SQL.

---

## Cấu trúc thư mục ảnh

```
src/main/resources/static/images/
├── hero-banner.jpg          ← Ảnh nền trang chủ (khuyến nghị: 1920×1080)
├── about-us.jpg             ← Ảnh giới thiệu cửa hàng (khuyến nghị: 800×600)
└── products/
    ├── banh-kem-dau-tay.jpg
    ├── banh-chocolate-truffle.jpg
    ├── banh-tiramisu.jpg
    ├── banh-mousse-tra-xanh.jpg
    ├── banh-croissant.jpg
    ├── banh-macaron.jpg
    ├── banh-muffin.jpg
    ├── tra-sua-tran-chau.jpg
    ├── ca-phe-sua-da.jpg
    └── tra-dao-cam-sa.jpg
```

## Bước thực hiện
1. Copy ảnh của bạn vào đúng đường dẫn trên
2. Đặt tên file **giống hệt** như danh sách (hoặc sửa tên trong `database-init.sql`)
3. Khuyến nghị định dạng: `.jpg` hoặc `.webp`, kích thước < 500KB mỗi ảnh
4. Tỉ lệ ảnh sản phẩm: **4:3** hoặc **1:1** (vuông) để không bị méo

## Lưu ý khi thêm sản phẩm mới qua Admin
- Upload ảnh vào thư mục `static/images/products/`
- Nhập đường dẫn dạng: `/images/products/ten-san-pham.jpg`

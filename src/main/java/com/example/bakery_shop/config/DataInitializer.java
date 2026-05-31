package com.example.bakery_shop.config;

import com.example.bakery_shop.entity.DanhMuc;
import com.example.bakery_shop.entity.NguoiDung;
import com.example.bakery_shop.entity.SanPham;
import com.example.bakery_shop.repository.DanhMucRepository;
import com.example.bakery_shop.repository.NguoiDungRepository;
import com.example.bakery_shop.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Tự động khởi tạo dữ liệu mẫu nếu cơ sở dữ liệu trống khi ứng dụng khởi chạy.
 * Đảm bảo luôn có tài khoản đăng nhập admin/user và danh sách sản phẩm mẫu.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final NguoiDungRepository nguoiDungRepository;
    private final DanhMucRepository danhMucRepository;
    private final SanPhamRepository sanPhamRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Tự động seed/cập nhật tài khoản NGUOIDUNG mẫu
        String encodedPassword = passwordEncoder.encode("123456");
        
        // Cập nhật hoặc tạo mới Admin
        java.util.Optional<NguoiDung> adminOpt = nguoiDungRepository.findByEmail("admin@bakery.com");
        if (adminOpt.isEmpty()) {
            NguoiDung admin = NguoiDung.builder()
                    .hoTen("Admin Tiệm Bánh")
                    .email("admin@bakery.com")
                    .matKhau(encodedPassword)
                    .soDienThoai("0901234567")
                    .diaChi("123 Đường Bánh Ngọt, Quận 1, TP.HCM")
                    .vaiTro("ROLE_ADMIN")
                    .build();
            nguoiDungRepository.save(admin);
            log.info("Tạo mới tài khoản Admin thành công: admin@bakery.com / 123456");
        } else {
            NguoiDung admin = adminOpt.get();
            admin.setMatKhau(encodedPassword);
            nguoiDungRepository.save(admin);
            log.info("Đã cập nhật lại mật khẩu chuẩn BCrypt cho Admin: admin@bakery.com / 123456");
        }

        // Cập nhật hoặc tạo mới Khách hàng mẫu
        java.util.Optional<NguoiDung> userOpt = nguoiDungRepository.findByEmail("user@bakery.com");
        if (userOpt.isEmpty()) {
            NguoiDung user = NguoiDung.builder()
                    .hoTen("Khách Hàng")
                    .email("user@bakery.com")
                    .matKhau(encodedPassword)
                    .soDienThoai("0909876543")
                    .diaChi("456 Đường Trà Sữa, Quận 3, TP.HCM")
                    .vaiTro("ROLE_USER")
                    .build();
            nguoiDungRepository.save(user);
            log.info("Tạo mới tài khoản Khách hàng thành công: user@bakery.com / 123456");
        } else {
            NguoiDung user = userOpt.get();
            user.setMatKhau(encodedPassword);
            nguoiDungRepository.save(user);
            log.info("Đã cập nhật lại mật khẩu chuẩn BCrypt cho Khách hàng: user@bakery.com / 123456");
        }

        // 2. Tự động seed danh mục (DANHMUC) và sản phẩm (SANPHAM) mẫu
        if (danhMucRepository.count() == 0) {
            log.info("DATABASE TRỐNG: Đang tiến hành tạo danh mục và sản phẩm mẫu...");

            DanhMuc banhKem = DanhMuc.builder()
                    .tenDanhMuc("Bánh kem")
                    .moTa("Các loại bánh kem sinh nhật, bánh sự kiện")
                    .icon("fa-solid fa-cake-candles")
                    .build();

            DanhMuc banhMiNgot = DanhMuc.builder()
                    .tenDanhMuc("Bánh mì ngọt")
                    .moTa("Bánh sừng bò, bánh mì ngọt mềm dùng hàng ngày")
                    .icon("fa-solid fa-bread-slice")
                    .build();

            DanhMuc doUong = DanhMuc.builder()
                    .tenDanhMuc("Đồ uống")
                    .moTa("Cà phê, trà trái cây và các loại nước giải khát")
                    .icon("fa-solid fa-mug-hot")
                    .build();

            banhKem = danhMucRepository.save(banhKem);
            banhMiNgot = danhMucRepository.save(banhMiNgot);
            doUong = danhMucRepository.save(doUong);

            // Bánh kem
            SanPham sp1 = SanPham.builder()
                    .danhMuc(banhKem)
                    .tenSanPham("Bánh Kem Dâu Tây Thượng Hạng")
                    .moTa("Bánh kem sữa tươi nguyên chất phủ dâu tây tươi mọng nước từ Đà Lạt. Vị ngọt thanh không ngấy.")
                    .gia(new BigDecimal("350000"))
                    .soLuongTon(20)
                    .hinhAnh("https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=600&auto=format&fit=crop&q=60")
                    .trangThai(true)
                    .build();

            SanPham sp2 = SanPham.builder()
                    .danhMuc(banhKem)
                    .tenSanPham("Bánh Chocolate Truffle")
                    .moTa("Bánh kem socola đậm vị, phủ bột cacao và socola bào nguyên chất Bỉ.")
                    .gia(new BigDecimal("420000"))
                    .soLuongTon(15)
                    .hinhAnh("https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=600&auto=format&fit=crop&q=60")
                    .trangThai(true)
                    .build();

            SanPham sp3 = SanPham.builder()
                    .danhMuc(banhKem)
                    .tenSanPham("Bánh Tiramisu Ý")
                    .moTa("Sự kết hợp hoàn hảo giữa phô mai mascarpone béo ngậy, cà phê espresso và bột cacao nguyên chất.")
                    .gia(new BigDecimal("380000"))
                    .soLuongTon(10)
                    .hinhAnh("https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=600&auto=format&fit=crop&q=60")
                    .trangThai(true)
                    .build();

            SanPham sp4 = SanPham.builder()
                    .danhMuc(banhKem)
                    .tenSanPham("Bánh Mousse Trà Xanh")
                    .moTa("Lớp mousse mềm mịn hòa quyện cùng hương vị matcha thanh mát chuẩn Nhật Bản.")
                    .gia(new BigDecimal("290000"))
                    .soLuongTon(25)
                    .hinhAnh("https://images.unsplash.com/photo-1536680465769-2365207b035e?w=600&auto=format&fit=crop&q=60")
                    .trangThai(true)
                    .build();

            // Bánh mì ngọt
            SanPham sp5 = SanPham.builder()
                    .danhMuc(banhMiNgot)
                    .tenSanPham("Bánh Croissant Bơ Pháp")
                    .moTa("Bánh sừng bò ngàn lớp thơm lừng bơ Pháp cao cấp. Phù hợp cho bữa sáng nhẹ nhàng.")
                    .gia(new BigDecimal("45000"))
                    .soLuongTon(50)
                    .hinhAnh("https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=600&auto=format&fit=crop&q=60")
                    .trangThai(true)
                    .build();

            SanPham sp6 = SanPham.builder()
                    .danhMuc(banhMiNgot)
                    .tenSanPham("Bánh Macaron Assorted")
                    .moTa("Hộp 6 chiếc bánh macaron đủ vị truyền thống Pháp: Vanilla, Socola, Dâu tây, Chanh, Matcha, Cà phê.")
                    .gia(new BigDecimal("150000"))
                    .soLuongTon(30)
                    .hinhAnh("https://images.unsplash.com/photo-1569864358642-9d1684040f43?w=600&auto=format&fit=crop&q=60")
                    .trangThai(true)
                    .build();

            SanPham sp7 = SanPham.builder()
                    .danhMuc(banhMiNgot)
                    .tenSanPham("Bánh Muffin Việt Quất")
                    .moTa("Bánh nướng xốp mềm hòa quyện với trái việt quất tươi mọng nước.")
                    .gia(new BigDecimal("35000"))
                    .soLuongTon(40)
                    .hinhAnh("https://images.unsplash.com/photo-1607958996333-41aef7caefaa?w=600&auto=format&fit=crop&q=60")
                    .trangThai(true)
                    .build();

            // Đồ uống
            SanPham sp8 = SanPham.builder()
                    .danhMuc(doUong)
                    .tenSanPham("Trà Sữa Trân Châu Hoàng Gia")
                    .moTa("Trà đen hảo hạng kết hợp sữa tươi thanh trùng và trân châu đường đen dẻo dai.")
                    .gia(new BigDecimal("45000"))
                    .soLuongTon(100)
                    .hinhAnh("https://images.unsplash.com/photo-1541658016709-82535e94bc69?w=600&auto=format&fit=crop&q=60")
                    .trangThai(true)
                    .build();

            SanPham sp9 = SanPham.builder()
                    .danhMuc(doUong)
                    .tenSanPham("Cà Phê Sữa Đá Sài Gòn")
                    .moTa("Cà phê rang xay đậm vị pha trộn cùng sữa đặc có đường theo chuẩn truyền thống Sài Gòn.")
                    .gia(new BigDecimal("35000"))
                    .soLuongTon(100)
                    .hinhAnh("https://images.unsplash.com/photo-1517701604599-bb29b565090c?w=600&auto=format&fit=crop&q=60")
                    .trangThai(true)
                    .build();

            SanPham sp10 = SanPham.builder()
                    .danhMuc(doUong)
                    .tenSanPham("Trà Đào Cam Sả")
                    .moTa("Thức uống giải nhiệt ngày hè với đào ngâm giòn ngọt, cam tươi vắt và sả thơm lừng.")
                    .gia(new BigDecimal("50000"))
                    .soLuongTon(80)
                    .hinhAnh("https://images.unsplash.com/photo-1497515114629-f71d768fd07c?w=600&auto=format&fit=crop&q=60")
                    .trangThai(true)
                    .build();

            sanPhamRepository.saveAll(List.of(sp1, sp2, sp3, sp4, sp5, sp6, sp7, sp8, sp9, sp10));
            log.info("Tạo danh mục và sản phẩm mẫu thành công!");
        }
    }
}

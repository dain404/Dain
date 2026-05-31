package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.NguoiDung;

import java.util.List;
import java.util.Optional;

/**
 * Interface service người dùng
 */
public interface NguoiDungService {

    // Đăng ký tài khoản mới (mã hóa BCrypt)
    NguoiDung dangKy(NguoiDung nguoiDung);

    // Tìm theo email
    Optional<NguoiDung> timTheoEmail(String email);

    // Tìm theo ID
    Optional<NguoiDung> timTheoId(Long userId);

    // Lấy tất cả người dùng
    List<NguoiDung> layTatCa();

    // Cập nhật thông tin cá nhân
    NguoiDung capNhatThongTin(NguoiDung nguoiDung);

    // Đổi mật khẩu
    void doiMatKhau(Long userId, String matKhauCu, String matKhauMoi);

    // Xóa tài khoản
    void xoa(Long userId);

    // Tìm kiếm người dùng theo tên/email
    List<NguoiDung> timKiem(String keyword);

    // Kiểm tra email đã tồn tại
    boolean emailDaTonTai(String email);
}

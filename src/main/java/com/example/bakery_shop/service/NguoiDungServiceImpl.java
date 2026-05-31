package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.NguoiDung;
import com.example.bakery_shop.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Triển khai service người dùng
 */
@Service
@RequiredArgsConstructor
public class NguoiDungServiceImpl implements NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public NguoiDung dangKy(NguoiDung nguoiDung) {
        // Kiểm tra email trùng
        if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng: " + nguoiDung.getEmail());
        }
        // Mã hóa mật khẩu BCrypt trước khi lưu
        nguoiDung.setMatKhau(passwordEncoder.encode(nguoiDung.getMatKhau()));
        // Gán vai trò mặc định nếu chưa có
        if (nguoiDung.getVaiTro() == null || nguoiDung.getVaiTro().isBlank()) {
            nguoiDung.setVaiTro("ROLE_USER");
        }
        return nguoiDungRepository.save(nguoiDung);
    }

    @Override
    public Optional<NguoiDung> timTheoEmail(String email) {
        return nguoiDungRepository.findByEmail(email);
    }

    @Override
    public Optional<NguoiDung> timTheoId(Long userId) {
        return nguoiDungRepository.findById(userId);
    }

    @Override
    public List<NguoiDung> layTatCa() {
        return nguoiDungRepository.findAll();
    }

    @Override
    @Transactional
    public NguoiDung capNhatThongTin(NguoiDung nguoiDung) {
        // Chỉ cập nhật thông tin, không đổi mật khẩu ở đây
        NguoiDung existing = nguoiDungRepository.findById(nguoiDung.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        existing.setHoTen(nguoiDung.getHoTen());
        existing.setSoDienThoai(nguoiDung.getSoDienThoai());
        existing.setDiaChi(nguoiDung.getDiaChi());
        return nguoiDungRepository.save(existing);
    }

    @Override
    @Transactional
    public void doiMatKhau(Long userId, String matKhauCu, String matKhauMoi) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        // Xác minh mật khẩu cũ
        if (!passwordEncoder.matches(matKhauCu, nguoiDung.getMatKhau())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }
        nguoiDung.setMatKhau(passwordEncoder.encode(matKhauMoi));
        nguoiDungRepository.save(nguoiDung);
    }

    @Override
    @Transactional
    public void xoa(Long userId) {
        nguoiDungRepository.deleteById(userId);
    }

    @Override
    public List<NguoiDung> timKiem(String keyword) {
        return nguoiDungRepository.timKiemNguoiDung(keyword);
    }

    @Override
    public boolean emailDaTonTai(String email) {
        return nguoiDungRepository.existsByEmail(email);
    }
}

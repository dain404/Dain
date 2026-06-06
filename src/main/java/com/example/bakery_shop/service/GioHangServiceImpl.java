package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.GioHang;
import com.example.bakery_shop.entity.NguoiDung;
import com.example.bakery_shop.entity.SanPham;
import com.example.bakery_shop.repository.GioHangRepository;
import com.example.bakery_shop.repository.NguoiDungRepository;
import com.example.bakery_shop.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Triển khai service giỏ hàng
 */
@Service
@RequiredArgsConstructor
public class GioHangServiceImpl implements GioHangService {

    private final GioHangRepository gioHangRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final SanPhamRepository sanPhamRepository;

    @Override
    public List<GioHang> layGioHang(Long userId) {
        return gioHangRepository.findByNguoiDungIdFetchSanPham(userId);
    }

    @Override
    @Transactional
    public GioHang themVaoGio(Long userId, Long sanPhamId, int soLuong) {
        SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (sanPham.getTrangThai() != null && !sanPham.getTrangThai()) {
            throw new RuntimeException("Sản phẩm hiện không còn bán");
        }
        if (sanPham.getSoLuongTon() != null && sanPham.getSoLuongTon() == 0) {
            throw new RuntimeException("Sản phẩm đã hết hàng");
        }

        // Nếu đã có sản phẩm trong giỏ → tăng số lượng
        Optional<GioHang> existing = gioHangRepository
                .findByNguoiDung_UserIdAndSanPham_SanPhamId(userId, sanPhamId);

        if (existing.isPresent()) {
            GioHang gioHang = existing.get();
            int soLuongMoi = gioHang.getSoLuong() + soLuong;
            if (sanPham.getSoLuongTon() != null && soLuongMoi > sanPham.getSoLuongTon()) {
                throw new RuntimeException("Chỉ còn " + sanPham.getSoLuongTon() + " sản phẩm trong kho");
            }
            gioHang.setSoLuong(soLuongMoi);
            return gioHangRepository.save(gioHang);
        }

        if (sanPham.getSoLuongTon() != null && soLuong > sanPham.getSoLuongTon()) {
            throw new RuntimeException("Chỉ còn " + sanPham.getSoLuongTon() + " sản phẩm trong kho");
        }

        // Chưa có → tạo mới
        NguoiDung nguoiDung = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        GioHang gioHang = GioHang.builder()
                .nguoiDung(nguoiDung)
                .sanPham(sanPham)
                .soLuong(soLuong)
                .build();
        return gioHangRepository.save(gioHang);
    }

    @Override
    @Transactional
    public GioHang capNhatSoLuong(Long gioHangId, int soLuongMoi) {
        GioHang gioHang = gioHangRepository.findById(gioHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy item giỏ hàng"));
        if (soLuongMoi <= 0) {
            gioHangRepository.delete(gioHang);
            return null;
        }
        gioHang.setSoLuong(soLuongMoi);
        return gioHangRepository.save(gioHang);
    }

    @Override
    @Transactional
    public void xoaKhoiGio(Long gioHangId) {
        gioHangRepository.deleteById(gioHangId);
    }

    @Override
    @Transactional
    public void xoaGioHang(Long userId) {
        gioHangRepository.xoaGioHangCuaNguoiDung(userId);
    }

    @Override
    public Long demSoItem(Long userId) {
        return gioHangRepository.countByNguoiDung_UserId(userId);
    }
}

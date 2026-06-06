package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.SanPham;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

/**
 * Interface service sản phẩm
 */
public interface SanPhamService {

    List<SanPham> layTatCa();

    Page<SanPham> layTatCa(int page, int size);

    List<SanPham> layDangBan();

    Optional<SanPham> timTheoId(Long id);

    List<SanPham> timKiem(String keyword);

    List<SanPham> layTheoanhMuc(Long danhMucId);

    List<SanPham> laySanPhamNoiBat();

    SanPham luu(SanPham sanPham);

    void xoa(Long id);

    // Cập nhật tồn kho sau khi đặt hàng
    void capNhatTonKho(Long sanPhamId, int soLuongMua);
}

package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.GioHang;

import java.util.List;

/**
 * Interface service giỏ hàng
 */
public interface GioHangService {

    // Lấy giỏ hàng của người dùng
    List<GioHang> layGioHang(Long userId);

    // Thêm sản phẩm vào giỏ (nếu đã có thì tăng số lượng)
    GioHang themVaoGio(Long userId, Long sanPhamId, int soLuong);

    // Cập nhật số lượng trong giỏ
    GioHang capNhatSoLuong(Long gioHangId, int soLuongMoi);

    // Xóa 1 item khỏi giỏ
    void xoaKhoiGio(Long gioHangId);

    // Xóa toàn bộ giỏ hàng của người dùng
    void xoaGioHang(Long userId);

    // Đếm số item trong giỏ
    Long demSoItem(Long userId);
}

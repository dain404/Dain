package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.DonHang;
import com.example.bakery_shop.entity.GioHang;
import com.example.bakery_shop.entity.TrangThaiDonHang;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interface service đơn hàng
 */
public interface DonHangService {

    // Tạo đơn hàng từ giỏ hàng
    DonHang datHang(DonHang donHang, List<GioHang> gioHangs, String phuongThucThanhToan);

    // Lấy lịch sử đơn hàng của người dùng
    List<DonHang> layDonHangCuaUser(Long userId);

    // Lấy chi tiết một đơn hàng
    Optional<DonHang> timTheoId(Long donHangId);

    // Lấy tất cả đơn hàng (admin)
    List<DonHang> layTatCa();

    // Cập nhật trạng thái đơn hàng
    DonHang capNhatTrangThai(Long donHangId, TrangThaiDonHang trangThaiMoi);

    // Hủy đơn hàng (chỉ khi CHO_XAC_NHAN)
    void huyDonHang(Long donHangId, Long userId);

    // Thống kê doanh thu tháng hiện tại
    BigDecimal doanhThuThangNay();

    // Đếm đơn theo trạng thái
    Long demDonTheoTrangThai(TrangThaiDonHang trangThai);
}

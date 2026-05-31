package com.example.bakery_shop.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO đơn hàng — dùng cho lịch sử đơn hàng và trang admin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonHangDTO {

    private Long donHangId;
    private String tenNguoiDung;
    private String emailNguoiDung;
    private String diaChiGiaoHang;
    private String soDienThoaiNhan;
    private BigDecimal tongTien;
    private String tongTienHienThi;     // Đã format: "250,000 đ"
    private String trangThai;           // Giá trị enum
    private String tenTrangThai;        // Tên tiếng Việt: "Đang giao"
    private String ngayDatHang;         // Đã format: "26/05/2026 07:30"
    private String ghiChu;
    private String phuongThucThanhToan;
    private List<ChiTietDonHangDTO> chiTiets;

    /**
     * DTO chi tiết dòng sản phẩm trong đơn hàng
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChiTietDonHangDTO {
        private Long chiTietId;
        private String tenSanPham;
        private String hinhAnh;
        private Integer soLuong;
        private BigDecimal donGia;
        private String donGiaHienThi;
        private BigDecimal thanhTien;
        private String thanhTienHienThi;
    }
}

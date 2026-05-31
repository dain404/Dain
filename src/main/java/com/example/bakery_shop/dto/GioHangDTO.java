package com.example.bakery_shop.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO item trong giỏ hàng — dùng cho trang cart và checkout
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GioHangDTO {

    private Long gioHangId;
    private Long sanPhamId;
    private String tenSanPham;
    private String hinhAnh;
    private BigDecimal donGia;
    private String donGiaHienThi;   // Giá đã format: "45,000 đ"
    private Integer soLuong;
    private BigDecimal thanhTien;
    private String thanhTienHienThi; // Thành tiền đã format
    private Integer soLuongTon;     // Tồn kho để validate ở UI
}

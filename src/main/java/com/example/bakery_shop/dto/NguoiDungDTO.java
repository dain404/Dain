package com.example.bakery_shop.dto;

import lombok.*;

/**
 * DTO thông tin người dùng — dùng cho trang profile và admin/users
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDungDTO {

    private Long userId;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private String vaiTro;
    private String ngayTao; // Formatted string để hiển thị

    // Số đơn hàng (dùng trong admin dashboard)
    private Long soDonHang;
}

package com.example.bakery_shop.dto;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * DTO form checkout — dùng cho trang đặt hàng /checkout
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutDTO {

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String diaChiGiaoHang;

    @NotBlank(message = "Số điện thoại nhận hàng không được để trống")
    @Pattern(regexp = "^(\\+84|0)\\d{9,10}$", message = "Số điện thoại không hợp lệ")
    private String soDienThoaiNhan;

    @NotBlank(message = "Vui lòng chọn phương thức thanh toán")
    private String phuongThucThanhToan; // COD, BANKING, MOMO

    private String ghiChu;
}

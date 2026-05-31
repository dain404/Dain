package com.example.bakery_shop.dto;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * DTO đăng ký tài khoản — dùng cho form /register
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DangKyDTO {

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    private String hoTen;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
    private String matKhau;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String xacNhanMatKhau;

    @Pattern(regexp = "^(\\+84|0)\\d{9,10}$", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    private String diaChi;
}

package com.example.bakery_shop.dto;

import lombok.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO sản phẩm — dùng cho danh sách, chi tiết, form admin thêm/sửa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamDTO {

    private Long sanPhamId;

    @NotNull(message = "Vui lòng chọn danh mục")
    private Long danhMucId;

    private String tenDanhMuc;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 200, message = "Tên sản phẩm tối đa 200 ký tự")
    private String tenSanPham;

    private String moTa;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "1000", message = "Giá tối thiểu 1,000 VNĐ")
    private BigDecimal gia;

    // Giá đã format (vd: "45,000 đ")
    private String giaHienThi;

    @Min(value = 0, message = "Số lượng tồn không được âm")
    private Integer soLuongTon;

    private String hinhAnh;

    private Boolean trangThai;
}

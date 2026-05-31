package com.example.bakery_shop.dto;

import lombok.*;

/**
 * DTO danh mục — dùng cho sidebar filter và form admin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DanhMucDTO {

    private Long danhMucId;
    private String tenDanhMuc;
    private String moTa;
    private String icon;            // FontAwesome class: "fa-birthday-cake"
    private Long soLuongSanPham;    // Số sản phẩm đang bán trong danh mục
}

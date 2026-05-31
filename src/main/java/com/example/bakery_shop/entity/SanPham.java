package com.example.bakery_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Entity sản phẩm bánh — ánh xạ bảng SANPHAM
 */
@Entity
@Table(name = "SANPHAM")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SanPham {

    // Khóa chính tự tăng
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sanPhamId")
    private Long sanPhamId;

    // Quan hệ N-1 với danh mục
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "danhMucId", nullable = false)
    @ToString.Exclude
    private DanhMuc danhMuc;

    // Tên sản phẩm
    @Column(name = "tenSanPham", nullable = false, columnDefinition = "NVARCHAR(200)")
    private String tenSanPham;

    // Mô tả chi tiết
    @Column(name = "moTa", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    // Giá bán (VNĐ)
    @Column(name = "gia", nullable = false, precision = 12, scale = 0)
    private BigDecimal gia;

    // Số lượng tồn kho
    @Column(name = "soLuongTon", nullable = false)
    @Builder.Default
    private Integer soLuongTon = 0;

    // URL hình ảnh sản phẩm
    @Column(name = "hinhAnh", length = 500)
    private String hinhAnh;

    // Trạng thái hiển thị (true = đang bán)
    @Column(name = "trangThai", nullable = false)
    @Builder.Default
    private Boolean trangThai = true;
}

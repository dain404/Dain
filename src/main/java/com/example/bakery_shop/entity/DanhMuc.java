package com.example.bakery_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

/**
 * Entity danh mục sản phẩm — ánh xạ bảng DANHMUC
 */
@Entity
@Table(name = "DANHMUC")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DanhMuc {

    // Khóa chính tự tăng
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "danhMucId")
    private Long danhMucId;

    // Tên danh mục (Bánh kem, Bánh mì ngọt, Đồ uống...)
    @Column(name = "tenDanhMuc", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String tenDanhMuc;

    // Mô tả ngắn danh mục
    @Column(name = "moTa", columnDefinition = "NVARCHAR(255)")
    private String moTa;

    // Icon FontAwesome class (vd: fa-birthday-cake)
    @Column(name = "icon", length = 100)
    private String icon;

    // Quan hệ 1-N với sản phẩm
    @OneToMany(mappedBy = "danhMuc", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<SanPham> sanPhams;
}

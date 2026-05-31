package com.example.bakery_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity giỏ hàng — ánh xạ bảng GIO_HANG
 */
@Entity
@Table(name = "GIO_HANG",
       uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "sanPhamId"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GioHang {

    // Khóa chính tự tăng
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gioHangId")
    private Long gioHangId;

    // Quan hệ N-1 với người dùng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    @ToString.Exclude
    private NguoiDung nguoiDung;

    // Quan hệ N-1 với sản phẩm
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sanPhamId", nullable = false)
    @ToString.Exclude
    private SanPham sanPham;

    // Số lượng trong giỏ
    @Column(name = "soLuong", nullable = false)
    @Builder.Default
    private Integer soLuong = 1;

    // Ngày thêm vào giỏ
    @Column(name = "ngayThem", nullable = false, updatable = false)
    private LocalDateTime ngayThem;

    @PrePersist
    protected void onCreate() {
        this.ngayThem = LocalDateTime.now();
    }
}

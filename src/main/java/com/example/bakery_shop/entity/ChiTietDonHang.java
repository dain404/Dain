package com.example.bakery_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Entity chi tiết đơn hàng — ánh xạ bảng CHITIET_DONHANG
 */
@Entity
@Table(name = "CHITIET_DONHANG")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDonHang {

    // Khóa chính tự tăng
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chiTietId")
    private Long chiTietId;

    // Quan hệ N-1 với đơn hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donHangId", nullable = false)
    @ToString.Exclude
    private DonHang donHang;

    // Quan hệ N-1 với sản phẩm
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sanPhamId", nullable = false)
    @ToString.Exclude
    private SanPham sanPham;

    // Số lượng mua
    @Column(name = "soLuong", nullable = false)
    private Integer soLuong;

    // Đơn giá tại thời điểm đặt
    @Column(name = "donGia", nullable = false, precision = 12, scale = 0)
    private BigDecimal donGia;

    // Thành tiền = soLuong * donGia
    @Column(name = "thanhTien", nullable = false, precision = 14, scale = 0)
    private BigDecimal thanhTien;
}

package com.example.bakery_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity thanh toán — ánh xạ bảng THANHTOAN
 */
@Entity
@Table(name = "THANHTOAN")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToan {

    // Khóa chính tự tăng
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thanhToanId")
    private Long thanhToanId;

    // Quan hệ 1-1 với đơn hàng
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donHangId", nullable = false)
    @ToString.Exclude
    private DonHang donHang;

    // Phương thức thanh toán: COD, BANKING, MOMO, VNPAY
    @Column(name = "phuongThucThanhToan", nullable = false, length = 50)
    private String phuongThucThanhToan;

    // Trạng thái: CHO_THANH_TOAN, DA_THANH_TOAN, THAT_BAI, HOAN_TIEN
    @Column(name = "trangThaiThanhToan", nullable = false, length = 30)
    @Builder.Default
    private String trangThaiThanhToan = "CHO_THANH_TOAN";

    // Số tiền thanh toán
    @Column(name = "soTienThanhToan", nullable = false, precision = 14, scale = 0)
    private BigDecimal soTienThanhToan;

    // Mã giao dịch (nếu có)
    @Column(name = "maGiaoDich", length = 100)
    private String maGiaoDich;

    // Ngày thanh toán thành công
    @Column(name = "ngayThanhToan")
    private LocalDateTime ngayThanhToan;

    // Ngày tạo bản ghi
    @Column(name = "ngayTao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }
}

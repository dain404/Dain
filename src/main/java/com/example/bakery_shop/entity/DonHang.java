package com.example.bakery_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity đơn hàng — ánh xạ bảng DONHANG
 */
@Entity
@Table(name = "DONHANG")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(com.example.bakery_shop.listener.DonHangListener.class)
public class DonHang {

    // Khóa chính tự tăng
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donHangId")
    private Long donHangId;

    // Quan hệ N-1 với người dùng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    @ToString.Exclude
    private NguoiDung nguoiDung;

    // Địa chỉ giao hàng
    @Column(name = "diaChiGiaoHang", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String diaChiGiaoHang;

    // Số điện thoại nhận hàng
    @Column(name = "soDienThoaiNhan", length = 15)
    private String soDienThoaiNhan;

    // Tổng tiền đơn hàng
    @Column(name = "tongTien", nullable = false, precision = 14, scale = 0)
    private BigDecimal tongTien;

    // Trạng thái đơn hàng (enum)
    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false, length = 20)
    @Builder.Default
    private TrangThaiDonHang trangThai = TrangThaiDonHang.CHO_XAC_NHAN;

    // Ghi chú của khách hàng
    @Column(name = "ghiChu", columnDefinition = "NVARCHAR(500)")
    private String ghiChu;

    // Ngày đặt hàng
    @Column(name = "ngayDatHang", nullable = false, updatable = false)
    private LocalDateTime ngayDatHang;

    // Ngày cập nhật trạng thái
    @Column(name = "ngayCapNhat")
    private LocalDateTime ngayCapNhat;

    // Chi tiết các sản phẩm trong đơn
    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ChiTietDonHang> chiTietDonHangs;

    // Thông tin thanh toán
    @OneToOne(mappedBy = "donHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private ThanhToan thanhToan;

    // Trường không lưu vào DB để nhớ trạng thái cũ
    @Transient
    private TrangThaiDonHang trangThaiCu;

    @PostLoad
    protected void onLoad() {
        this.trangThaiCu = this.trangThai;
    }

    @PrePersist
    protected void onCreate() {
        this.ngayDatHang = LocalDateTime.now();
        this.ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.ngayCapNhat = LocalDateTime.now();
    }
}

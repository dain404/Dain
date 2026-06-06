package com.example.bakery_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LICH_SU_DON_HANG")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichSuDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "don_hang_id", nullable = false)
    private Long donHangId;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_cu")
    private TrangThaiDonHang trangThaiCu;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_moi", nullable = false)
    private TrangThaiDonHang trangThaiMoi;

    @Column(name = "nguoi_thuc_hien")
    private String nguoiThucHien;

    @Column(name = "thoi_gian", nullable = false)
    private LocalDateTime thoiGian;
}

package com.example.bakery_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity người dùng — ánh xạ bảng NGUOIDUNG
 */
@Entity
@Table(name = "NGUOIDUNG")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDung {

    // Khóa chính tự tăng
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long userId;

    // Họ tên đầy đủ
    @Column(name = "hoTen", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String hoTen;

    // Email duy nhất — dùng để đăng nhập
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    // Mật khẩu đã mã hóa BCrypt
    @Column(name = "matKhau", nullable = false)
    private String matKhau;

    // Số điện thoại
    @Column(name = "soDienThoai", length = 15)
    private String soDienThoai;

    // Địa chỉ giao hàng mặc định
    @Column(name = "diaChi", columnDefinition = "NVARCHAR(255)")
    private String diaChi;

    // Vai trò: ROLE_USER hoặc ROLE_ADMIN
    @Column(name = "vaiTro", nullable = false, length = 20)
    @Builder.Default
    private String vaiTro = "ROLE_USER";

    // Ngày tạo tài khoản
    @Column(name = "ngayTao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    // Tự động gán ngày tạo trước khi persist
    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }
}

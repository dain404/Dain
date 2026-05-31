package com.example.bakery_shop.repository;

import com.example.bakery_shop.entity.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository thanh toán — truy vấn bảng THANHTOAN
 */
@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, Long> {

    // Tìm thanh toán theo đơn hàng
    Optional<ThanhToan> findByDonHang_DonHangId(Long donHangId);

    // Tìm thanh toán theo mã giao dịch (VNPay, MoMo...)
    Optional<ThanhToan> findByMaGiaoDich(String maGiaoDich);

    // Kiểm tra đơn hàng đã thanh toán chưa
    boolean existsByDonHang_DonHangIdAndTrangThaiThanhToan(Long donHangId, String trangThai);
}

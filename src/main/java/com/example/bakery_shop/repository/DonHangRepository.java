package com.example.bakery_shop.repository;

import com.example.bakery_shop.entity.DonHang;
import com.example.bakery_shop.entity.TrangThaiDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository đơn hàng — truy vấn bảng DONHANG
 */
@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {

    // Lấy đơn hàng theo người dùng, mới nhất trước
    List<DonHang> findByNguoiDung_UserIdOrderByNgayDatHangDesc(Long userId);

    // Lấy đơn hàng theo trạng thái
    List<DonHang> findByTrangThaiOrderByNgayDatHangDesc(TrangThaiDonHang trangThai);

    // Lấy đơn hàng của người dùng theo trạng thái
    List<DonHang> findByNguoiDung_UserIdAndTrangThai(Long userId, TrangThaiDonHang trangThai);

    // Tổng doanh thu trong khoảng thời gian (JPQL)
    @Query("SELECT COALESCE(SUM(d.tongTien), 0) FROM DonHang d WHERE d.trangThai = com.example.bakery_shop.entity.TrangThaiDonHang.HOAN_THANH AND d.ngayDatHang BETWEEN :tuNgay AND :denNgay")
    BigDecimal tinhDoanhThu(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);

    // Đếm số đơn theo trạng thái
    Long countByTrangThai(TrangThaiDonHang trangThai);

    // Lấy tất cả đơn hàng, mới nhất trước (admin)
    List<DonHang> findAllByOrderByNgayDatHangDesc();

    // Thống kê doanh thu theo tháng trong năm
    @Query("SELECT MONTH(d.ngayDatHang), SUM(d.tongTien) FROM DonHang d WHERE d.trangThai = com.example.bakery_shop.entity.TrangThaiDonHang.HOAN_THANH AND YEAR(d.ngayDatHang) = :nam GROUP BY MONTH(d.ngayDatHang) ORDER BY MONTH(d.ngayDatHang)")
    List<Object[]> thongKeDoanhThuTheoThang(@Param("nam") int nam);
}

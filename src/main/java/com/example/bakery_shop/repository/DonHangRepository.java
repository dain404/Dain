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

    // Lấy 5 đơn mới nhất
    List<DonHang> findTop5ByOrderByNgayDatHangDesc();

    // Thống kê doanh thu theo tháng trong năm
    @Query("SELECT MONTH(d.ngayDatHang), SUM(d.tongTien) FROM DonHang d WHERE d.trangThai = com.example.bakery_shop.entity.TrangThaiDonHang.HOAN_THANH AND YEAR(d.ngayDatHang) = :nam GROUP BY MONTH(d.ngayDatHang) ORDER BY MONTH(d.ngayDatHang)")
    List<Object[]> thongKeDoanhThuTheoThang(@Param("nam") int nam);
    List<DonHang> findByTrangThaiAndNgayDatHangBefore(TrangThaiDonHang trangThai, LocalDateTime threshold);

    @Query("SELECT SUM(d.tongTien) FROM DonHang d WHERE d.trangThai = :tt AND d.ngayDatHang BETWEEN :from AND :to")
    BigDecimal tinhDoanhThu(@Param("tt") TrangThaiDonHang tt, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT CAST(ngayDatHang AS DATE) as ngay, SUM(tongTien) as tong " +
                   "FROM DONHANG WHERE trangThai = 'HOAN_THANH' AND ngayDatHang >= :tuNgay " +
                   "GROUP BY CAST(ngayDatHang AS DATE) ORDER BY ngay ASC", nativeQuery = true)
    List<Object[]> thongKeDoanhThu7NgayQua(@Param("tuNgay") LocalDateTime tuNgay);
}

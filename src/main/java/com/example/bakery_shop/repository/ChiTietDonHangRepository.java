package com.example.bakery_shop.repository;

import com.example.bakery_shop.entity.ChiTietDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository chi tiết đơn hàng — truy vấn bảng CHITIET_DONHANG
 */
@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Long> {

    // Lấy chi tiết theo đơn hàng
    List<ChiTietDonHang> findByDonHang_DonHangId(Long donHangId);

    // Lấy chi tiết kèm thông tin sản phẩm (JPQL fetch join)
    @Query("SELECT c FROM ChiTietDonHang c JOIN FETCH c.sanPham WHERE c.donHang.donHangId = :donHangId")
    List<ChiTietDonHang> findByDonHangIdFetchSanPham(@Param("donHangId") Long donHangId);

    // Thống kê sản phẩm bán chạy (top N theo tổng số lượng)
    @Query("SELECT c.sanPham.sanPhamId, c.sanPham.tenSanPham, SUM(c.soLuong) as tongSoLuong FROM ChiTietDonHang c GROUP BY c.sanPham.sanPhamId, c.sanPham.tenSanPham ORDER BY tongSoLuong DESC")
    List<Object[]> thongKeSanPhamBanChay();
}

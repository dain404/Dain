package com.example.bakery_shop.repository;

import com.example.bakery_shop.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository sản phẩm — truy vấn bảng SANPHAM
 */
@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {

    // Lấy sản phẩm đang bán theo danh mục
    List<SanPham> findByDanhMuc_DanhMucIdAndTrangThaiTrue(Long danhMucId);

    // Lấy tất cả sản phẩm đang bán
    List<SanPham> findByTrangThaiTrue();

    // Tìm kiếm theo tên sản phẩm (JPQL, không phân biệt hoa thường)
    @Query("SELECT s FROM SanPham s WHERE LOWER(s.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) AND s.trangThai = true")
    List<SanPham> timKiemSanPham(@Param("keyword") String keyword);

    // Lấy sản phẩm nổi bật (tồn kho > 0, đang bán, lấy tối đa N sản phẩm)
    @Query("SELECT s FROM SanPham s WHERE s.trangThai = true AND s.soLuongTon > 0 ORDER BY s.sanPhamId DESC")
    List<SanPham> findSanPhamNoiBat();

    // Lấy sản phẩm theo danh mục (JPQL với JOIN)
    @Query("SELECT s FROM SanPham s JOIN s.danhMuc d WHERE d.danhMucId = :danhMucId AND s.trangThai = true")
    List<SanPham> findByDanhMucId(@Param("danhMucId") Long danhMucId);

    // Kiểm tra sản phẩm còn đủ tồn kho
    @Query("SELECT s FROM SanPham s WHERE s.sanPhamId = :id AND s.soLuongTon >= :soLuong")
    java.util.Optional<SanPham> findByIdVaDuTonKho(@Param("id") Long id, @Param("soLuong") Integer soLuong);
}

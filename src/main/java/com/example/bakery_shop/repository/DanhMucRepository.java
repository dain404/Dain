package com.example.bakery_shop.repository;

import com.example.bakery_shop.entity.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository danh mục — truy vấn bảng DANHMUC
 */
@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Long> {

    // Tìm danh mục theo tên
    Optional<DanhMuc> findByTenDanhMuc(String tenDanhMuc);

    // Lấy danh mục kèm số lượng sản phẩm (JPQL)
    @Query("SELECT d FROM DanhMuc d LEFT JOIN FETCH d.sanPhams WHERE d.danhMucId = :id")
    Optional<DanhMuc> findByIdWithSanPhams(Long id);

    // Lấy tất cả danh mục có sản phẩm đang bán
    @Query("SELECT DISTINCT d FROM DanhMuc d JOIN d.sanPhams s WHERE s.trangThai = true")
    List<DanhMuc> findDanhMucCoSanPhamDangBan();
}

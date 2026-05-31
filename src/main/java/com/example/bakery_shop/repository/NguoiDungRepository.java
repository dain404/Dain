package com.example.bakery_shop.repository;

import com.example.bakery_shop.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository người dùng — truy vấn bảng NGUOIDUNG
 */
@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {

    // Tìm người dùng theo email (dùng cho đăng nhập)
    Optional<NguoiDung> findByEmail(String email);

    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);

    // Tìm tất cả người dùng theo vai trò
    List<NguoiDung> findByVaiTro(String vaiTro);

    // Tìm kiếm người dùng theo tên hoặc email (JPQL)
    @Query("SELECT n FROM NguoiDung n WHERE LOWER(n.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(n.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<NguoiDung> timKiemNguoiDung(@Param("keyword") String keyword);
}

package com.example.bakery_shop.repository;

import com.example.bakery_shop.entity.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository giỏ hàng — truy vấn bảng GIO_HANG
 */
@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Long> {

    // Lấy toàn bộ giỏ hàng của người dùng, kèm thông tin sản phẩm
    @Query("SELECT g FROM GioHang g JOIN FETCH g.sanPham WHERE g.nguoiDung.userId = :userId")
    List<GioHang> findByNguoiDungIdFetchSanPham(@Param("userId") Long userId);

    // Tìm item trong giỏ theo userId + sanPhamId
    Optional<GioHang> findByNguoiDung_UserIdAndSanPham_SanPhamId(Long userId, Long sanPhamId);

    // Đếm số item trong giỏ hàng
    Long countByNguoiDung_UserId(Long userId);

    // Xóa toàn bộ giỏ hàng của người dùng (sau khi đặt hàng)
    @Modifying
    @Query("DELETE FROM GioHang g WHERE g.nguoiDung.userId = :userId")
    void xoaGioHangCuaNguoiDung(@Param("userId") Long userId);
}

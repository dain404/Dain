package com.example.bakery_shop.repository;

import com.example.bakery_shop.entity.LichSuDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuDonHangRepository extends JpaRepository<LichSuDonHang, Long> {
    List<LichSuDonHang> findByDonHangIdOrderByThoiGianDesc(Long donHangId);
}

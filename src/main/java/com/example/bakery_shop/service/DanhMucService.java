package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.DanhMuc;

import java.util.List;
import java.util.Optional;

/**
 * Interface service danh mục
 */
public interface DanhMucService {

    List<DanhMuc> layTatCa();

    Optional<DanhMuc> timTheoId(Long id);

    DanhMuc luu(DanhMuc danhMuc);

    void xoa(Long id);

    List<DanhMuc> layDanhMucCoSanPhamDangBan();
}

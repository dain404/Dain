package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.DanhMuc;
import com.example.bakery_shop.repository.DanhMucRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Triển khai service danh mục
 */
@Service
@RequiredArgsConstructor
public class DanhMucServiceImpl implements DanhMucService {

    private final DanhMucRepository danhMucRepository;

    @Override
    public List<DanhMuc> layTatCa() {
        return danhMucRepository.findAll();
    }

    @Override
    public Optional<DanhMuc> timTheoId(Long id) {
        return danhMucRepository.findById(id);
    }

    @Override
    @Transactional
    public DanhMuc luu(DanhMuc danhMuc) {
        return danhMucRepository.save(danhMuc);
    }

    @Override
    @Transactional
    public void xoa(Long id) {
        danhMucRepository.deleteById(id);
    }

    @Override
    public List<DanhMuc> layDanhMucCoSanPhamDangBan() {
        return danhMucRepository.findDanhMucCoSanPhamDangBan();
    }
}

package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.SanPham;
import com.example.bakery_shop.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Triển khai service sản phẩm
 */
@Service
@RequiredArgsConstructor
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository sanPhamRepository;

    @Override
    @Cacheable("san-pham")
    public List<SanPham> layTatCa() {
        return sanPhamRepository.findAll();
    }

    @Override
    public Page<SanPham> layTatCa(int page, int size) {
        return sanPhamRepository.findAll(PageRequest.of(page, size, Sort.by("sanPhamId").descending()));
    }

    @Override
    public List<SanPham> layDangBan() {
        return sanPhamRepository.findByTrangThaiTrue();
    }

    @Override
    @Cacheable(value="san-pham", key="#id")
    public Optional<SanPham> timTheoId(Long id) {
        return sanPhamRepository.findById(id);
    }

    @Override
    public List<SanPham> timKiem(String keyword) {
        return sanPhamRepository.timKiemSanPham(keyword);
    }

    @Override
    public List<SanPham> layTheoanhMuc(Long danhMucId) {
        return sanPhamRepository.findByDanhMucId(danhMucId);
    }

    @Override
    public List<SanPham> laySanPhamNoiBat() {
        return sanPhamRepository.findSanPhamNoiBat();
    }

    @Override
    @Transactional
    @CacheEvict(value="san-pham", allEntries=true)
    public SanPham luu(SanPham sanPham) {
        return sanPhamRepository.save(sanPham);
    }

    @Override
    @Transactional
    @CacheEvict(value="san-pham", allEntries=true)
    public void xoa(Long id) {
        // Ẩn thay vì xóa cứng để bảo toàn lịch sử đơn hàng
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + id));
        sanPham.setTrangThai(false);
        sanPhamRepository.save(sanPham);
    }

    @Override
    @Transactional
    public void capNhatTonKho(Long sanPhamId, int soLuongMua) {
        SanPham sanPham = sanPhamRepository.findById(sanPhamId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + sanPhamId));
        int tonKhoMoi = sanPham.getSoLuongTon() - soLuongMua;
        if (tonKhoMoi < 0) {
            throw new RuntimeException("Sản phẩm '" + sanPham.getTenSanPham() + "' không đủ tồn kho");
        }
        sanPham.setSoLuongTon(tonKhoMoi);
        sanPhamRepository.save(sanPham);
    }
}

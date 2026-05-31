package com.example.bakery_shop.controller;

import com.example.bakery_shop.service.DanhMucService;
import com.example.bakery_shop.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller sản phẩm — xử lý /products/**
 */
@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;
    private final DanhMucService danhMucService;

    /**
     * Danh sách sản phẩm — hỗ trợ lọc danh mục và tìm kiếm
     */
    @GetMapping
    public String danhSachSanPham(
            @RequestParam(required = false) Long danhMucId,
            @RequestParam(required = false) String keyword,
            Model model) {

        var sanPhams = (keyword != null && !keyword.isBlank())
                ? sanPhamService.timKiem(keyword)
                : (danhMucId != null)
                    ? sanPhamService.layTheoanhMuc(danhMucId)
                    : sanPhamService.layDangBan();

        model.addAttribute("sanPhams", sanPhams);
        model.addAttribute("danhMucs", danhMucService.layTatCa());
        model.addAttribute("danhMucIdHienTai", danhMucId);
        model.addAttribute("keyword", keyword);
        return "user/products";
    }

    /**
     * Chi tiết sản phẩm theo ID
     */
    @GetMapping("/{id}")
    public String chiTietSanPham(@PathVariable Long id, Model model) {
        var sanPham = sanPhamService.timTheoId(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + id));
        // Gợi ý sản phẩm cùng danh mục (tối đa 4)
        var sanPhamGoiY = sanPhamService.layTheoanhMuc(sanPham.getDanhMuc().getDanhMucId())
                .stream()
                .filter(sp -> !sp.getSanPhamId().equals(id))
                .limit(4)
                .toList();

        model.addAttribute("sanPham", sanPham);
        model.addAttribute("sanPhamGoiY", sanPhamGoiY);
        return "user/product-detail";
    }
}

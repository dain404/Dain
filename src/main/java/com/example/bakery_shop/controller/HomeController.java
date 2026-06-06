package com.example.bakery_shop.controller;

import com.example.bakery_shop.service.DanhMucService;
import com.example.bakery_shop.service.SanPhamService;
import com.example.bakery_shop.service.TyGiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller trang chủ — xử lý route /
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SanPhamService sanPhamService;
    private final DanhMucService danhMucService;
    private final TyGiaService tyGiaService;

    /**
     * Trang chủ: hero banner + sản phẩm nổi bật + danh mục
     */
    @GetMapping("/")
    public String trangChu(Model model) {
        // Lấy tối đa 8 sản phẩm nổi bật
        var sanPhamNoiBat = sanPhamService.laySanPhamNoiBat();
        if (sanPhamNoiBat.size() > 8) {
            sanPhamNoiBat = sanPhamNoiBat.subList(0, 8);
        }
        model.addAttribute("sanPhamNoiBat", sanPhamNoiBat);
        model.addAttribute("danhMucs", danhMucService.layTatCa());
        
        java.math.BigDecimal tyGia = tyGiaService.layTyGiaUSD();
        model.addAttribute("tyGia", tyGia);
        model.addAttribute("tyGiaFormatted", "1 USD ≈ " + java.text.NumberFormat.getInstance(java.util.Locale.of("vi", "VN")).format(tyGia) + " VNĐ");

        return "user/index";
    }

    /**
     * Trang giới thiệu shop
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}

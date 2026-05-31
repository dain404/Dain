package com.example.bakery_shop.controller;

import com.example.bakery_shop.service.DanhMucService;
import com.example.bakery_shop.service.SanPhamService;
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

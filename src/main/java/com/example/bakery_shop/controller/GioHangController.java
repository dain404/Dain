package com.example.bakery_shop.controller;

import com.example.bakery_shop.entity.NguoiDung;
import com.example.bakery_shop.service.GioHangService;
import com.example.bakery_shop.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller giỏ hàng — xử lý /cart/**
 */
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class GioHangController {

    private final GioHangService gioHangService;
    private final NguoiDungService nguoiDungService;

    /**
     * Lấy ID người dùng từ Authentication (email trong SecurityContext)
     */
    private Long layUserId(Authentication auth) {
        return nguoiDungService.timTheoEmail(auth.getName())
                .map(NguoiDung::getUserId)
                .orElseThrow(() -> new RuntimeException("Không xác định được người dùng"));
    }

    /**
     * Xem giỏ hàng
     */
    @GetMapping
    public String xemGioHang(Authentication auth, Model model) {
        Long userId = layUserId(auth);
        var gioHang = gioHangService.layGioHang(userId);

        // Tính tổng tiền
        var tongTien = gioHang.stream()
                .mapToLong(g -> g.getSanPham().getGia().longValue() * g.getSoLuong())
                .sum();

        model.addAttribute("gioHang", gioHang);
        model.addAttribute("tongTien", tongTien);
        return "user/cart";
    }

    /**
     * Thêm sản phẩm vào giỏ (AJAX hoặc form submit)
     */
    @PostMapping("/add")
    public String themVaoGio(@RequestParam Long sanPhamId,
                              @RequestParam(defaultValue = "1") int soLuong,
                              Authentication auth,
                              RedirectAttributes redirectAttrs) {
        Long userId = layUserId(auth);
        gioHangService.themVaoGio(userId, sanPhamId, soLuong);
        redirectAttrs.addFlashAttribute("thongBao", "Đã thêm vào giỏ hàng!");
        return "redirect:/cart";
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ
     */
    @PostMapping("/update")
    public String capNhatSoLuong(@RequestParam Long gioHangId,
                                  @RequestParam int soLuong,
                                  RedirectAttributes redirectAttrs) {
        gioHangService.capNhatSoLuong(gioHangId, soLuong);
        redirectAttrs.addFlashAttribute("thongBao", "Đã cập nhật giỏ hàng!");
        return "redirect:/cart";
    }

    /**
     * Xóa sản phẩm khỏi giỏ
     */
    @PostMapping("/remove")
    public String xoaKhoiGio(@RequestParam Long gioHangId,
                               RedirectAttributes redirectAttrs) {
        gioHangService.xoaKhoiGio(gioHangId);
        redirectAttrs.addFlashAttribute("thongBao", "Đã xóa sản phẩm khỏi giỏ hàng!");
        return "redirect:/cart";
    }
}

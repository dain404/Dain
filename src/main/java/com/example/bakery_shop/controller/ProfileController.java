package com.example.bakery_shop.controller;

import com.example.bakery_shop.entity.NguoiDung;
import com.example.bakery_shop.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller hồ sơ người dùng — xử lý /profile/**
 */
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final NguoiDungService nguoiDungService;

    /**
     * Lấy NguoiDung từ Authentication
     */
    private NguoiDung layNguoiDung(Authentication auth) {
        return nguoiDungService.timTheoEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Không xác định được người dùng"));
    }

    /**
     * Trang thông tin cá nhân
     */
    @GetMapping
    public String trangProfile(Authentication auth, Model model) {
        model.addAttribute("nguoiDung", layNguoiDung(auth));
        return "user/profile";
    }

    /**
     * Cập nhật thông tin cá nhân
     */
    @PostMapping("/update")
    public String capNhatThongTin(@ModelAttribute NguoiDung nguoiDung,
                                   Authentication auth,
                                   RedirectAttributes redirectAttrs) {
        NguoiDung existing = layNguoiDung(auth);
        nguoiDung.setUserId(existing.getUserId());
        nguoiDungService.capNhatThongTin(nguoiDung);
        redirectAttrs.addFlashAttribute("thanhCong", "Cập nhật thông tin thành công!");
        return "redirect:/profile";
    }

    /**
     * Đổi mật khẩu
     */
    @PostMapping("/password")
    public String doiMatKhau(@RequestParam String matKhauCu,
                              @RequestParam String matKhauMoi,
                              @RequestParam String xacNhanMatKhau,
                              Authentication auth,
                              RedirectAttributes redirectAttrs) {
        if (!matKhauMoi.equals(xacNhanMatKhau)) {
            redirectAttrs.addFlashAttribute("loi", "Mật khẩu xác nhận không khớp!");
            return "redirect:/profile";
        }
        NguoiDung nguoiDung = layNguoiDung(auth);
        nguoiDungService.doiMatKhau(nguoiDung.getUserId(), matKhauCu, matKhauMoi);
        redirectAttrs.addFlashAttribute("thanhCong", "Đổi mật khẩu thành công!");
        return "redirect:/profile";
    }
}

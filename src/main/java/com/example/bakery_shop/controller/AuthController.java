package com.example.bakery_shop.controller;

import com.example.bakery_shop.entity.NguoiDung;
import com.example.bakery_shop.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller xác thực — xử lý /login, /register
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final NguoiDungService nguoiDungService;

    /**
     * Hiển thị trang đăng nhập
     */
    @GetMapping("/login")
    public String trangDangNhap() {
        return "login";
    }

    /**
     * Hiển thị trang đăng ký
     */
    @GetMapping("/register")
    public String trangDangKy(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());
        return "register";
    }

    /**
     * Xử lý đăng ký tài khoản mới
     */
    @PostMapping("/register")
    public String xuLyDangKy(@ModelAttribute NguoiDung nguoiDung,
                               RedirectAttributes redirectAttrs) {
        // Kiểm tra email đã tồn tại
        if (nguoiDungService.emailDaTonTai(nguoiDung.getEmail())) {
            redirectAttrs.addFlashAttribute("loi", "Email này đã được đăng ký. Vui lòng dùng email khác.");
            return "redirect:/register";
        }
        // Gán vai trò mặc định
        nguoiDung.setVaiTro("ROLE_USER");
        nguoiDungService.dangKy(nguoiDung);
        redirectAttrs.addFlashAttribute("thanhCong", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "redirect:/login";
    }
}

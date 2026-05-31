package com.example.bakery_shop.controller;

import com.example.bakery_shop.entity.DonHang;
import com.example.bakery_shop.entity.NguoiDung;
import com.example.bakery_shop.service.DonHangService;
import com.example.bakery_shop.service.GioHangService;
import com.example.bakery_shop.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller đơn hàng — xử lý /orders/**, /checkout/**
 */
@Controller
@RequiredArgsConstructor
public class DonHangController {

    private final DonHangService donHangService;
    private final GioHangService gioHangService;
    private final NguoiDungService nguoiDungService;

    /**
     * Lấy entity NguoiDung từ Authentication
     */
    private NguoiDung layNguoiDung(Authentication auth) {
        return nguoiDungService.timTheoEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Không xác định được người dùng"));
    }

    /**
     * Trang thanh toán — hiển thị giỏ hàng + form địa chỉ
     */
    @GetMapping("/checkout")
    public String trangThanhToan(Authentication auth, Model model) {
        NguoiDung nguoiDung = layNguoiDung(auth);
        var gioHang = gioHangService.layGioHang(nguoiDung.getUserId());

        if (gioHang.isEmpty()) {
            return "redirect:/cart";
        }

        var tongTien = gioHang.stream()
                .mapToLong(g -> g.getSanPham().getGia().longValue() * g.getSoLuong())
                .sum();

        model.addAttribute("gioHang", gioHang);
        model.addAttribute("tongTien", tongTien);
        model.addAttribute("nguoiDung", nguoiDung);
        return "user/checkout";
    }

    /**
     * Xử lý đặt hàng — POST /checkout
     */
    @PostMapping("/checkout")
    public String datHang(@RequestParam String diaChiGiaoHang,
                           @RequestParam String soDienThoaiNhan,
                           @RequestParam String phuongThucThanhToan,
                           @RequestParam(required = false) String ghiChu,
                           Authentication auth,
                           RedirectAttributes redirectAttrs) {
        NguoiDung nguoiDung = layNguoiDung(auth);
        var gioHang = gioHangService.layGioHang(nguoiDung.getUserId());

        if (gioHang.isEmpty()) {
            return "redirect:/cart";
        }

        DonHang donHang = DonHang.builder()
                .nguoiDung(nguoiDung)
                .diaChiGiaoHang(diaChiGiaoHang)
                .soDienThoaiNhan(soDienThoaiNhan)
                .ghiChu(ghiChu)
                .build();

        DonHang donHangMoi = donHangService.datHang(donHang, gioHang, phuongThucThanhToan);
        // Xóa giỏ hàng sau khi đặt thành công
        gioHangService.xoaGioHang(nguoiDung.getUserId());

        redirectAttrs.addFlashAttribute("thongBaoDatHang",
                "Đặt hàng thành công! Mã đơn hàng: #" + donHangMoi.getDonHangId());
        return "redirect:/orders";
    }

    /**
     * Lịch sử đơn hàng của người dùng
     */
    @GetMapping("/orders")
    public String lichSuDonHang(Authentication auth, Model model) {
        NguoiDung nguoiDung = layNguoiDung(auth);
        model.addAttribute("donHangs", donHangService.layDonHangCuaUser(nguoiDung.getUserId()));
        return "user/order-history";
    }

    /**
     * Chi tiết một đơn hàng
     */
    @GetMapping("/orders/{id}")
    public String chiTietDonHang(@PathVariable Long id, Authentication auth, Model model) {
        NguoiDung nguoiDung = layNguoiDung(auth);
        var donHang = donHangService.timTheoId(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + id));

        // Kiểm tra quyền xem đơn hàng (chỉ chủ đơn hoặc admin)
        boolean laAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!laAdmin && !donHang.getNguoiDung().getUserId().equals(nguoiDung.getUserId())) {
            return "redirect:/orders";
        }

        model.addAttribute("donHang", donHang);
        return "user/order-history";
    }

    /**
     * Hủy đơn hàng (chỉ khi CHO_XAC_NHAN)
     */
    @PostMapping("/orders/{id}/huy")
    public String huyDonHang(@PathVariable Long id, Authentication auth,
                              RedirectAttributes redirectAttrs) {
        NguoiDung nguoiDung = layNguoiDung(auth);
        donHangService.huyDonHang(id, nguoiDung.getUserId());
        redirectAttrs.addFlashAttribute("thongBao", "Đã hủy đơn hàng #" + id);
        return "redirect:/orders";
    }
}

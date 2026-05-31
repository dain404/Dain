package com.example.bakery_shop.controller;

import com.example.bakery_shop.entity.DanhMuc;
import com.example.bakery_shop.entity.SanPham;
import com.example.bakery_shop.entity.TrangThaiDonHang;
import com.example.bakery_shop.service.DanhMucService;
import com.example.bakery_shop.service.DonHangService;
import com.example.bakery_shop.service.NguoiDungService;
import com.example.bakery_shop.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller quản trị — xử lý /admin/**
 * Chỉ dành cho ROLE_ADMIN (đã cấu hình trong SecurityConfig)
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SanPhamService sanPhamService;
    private final DanhMucService danhMucService;
    private final DonHangService donHangService;
    private final NguoiDungService nguoiDungService;

    /**
     * Dashboard — thống kê tổng quan
     */
    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        model.addAttribute("tongSanPham", sanPhamService.layTatCa().size());
        model.addAttribute("tongNguoiDung", nguoiDungService.layTatCa().size());
        model.addAttribute("donChoChatNhan", donHangService.demDonTheoTrangThai(TrangThaiDonHang.CHO_XAC_NHAN));
        model.addAttribute("donHoanThanh", donHangService.demDonTheoTrangThai(TrangThaiDonHang.HOAN_THANH));
        model.addAttribute("doanhThuThangNay", donHangService.doanhThuThangNay());
        // 5 đơn mới nhất
        var tatCaDon = donHangService.layTatCa();
        model.addAttribute("donHangMoiNhat", tatCaDon.stream().limit(5).toList());
        return "admin/dashboard";
    }

    // ===================== QUẢN LÝ SẢN PHẨM =====================

    /**
     * Danh sách sản phẩm + form thêm mới
     */
    @GetMapping("/products")
    public String quanLySanPham(Model model) {
        model.addAttribute("sanPhams", sanPhamService.layTatCa());
        model.addAttribute("danhMucs", danhMucService.layTatCa());
        model.addAttribute("sanPhamMoi", new SanPham());
        return "admin/products-manage";
    }

    /**
     * Lưu sản phẩm (thêm mới hoặc cập nhật)
     */
    @PostMapping("/products/save")
    public String luuSanPham(@ModelAttribute SanPham sanPham,
                              @RequestParam Long danhMucId,
                              RedirectAttributes redirectAttrs) {
        danhMucService.timTheoId(danhMucId).ifPresent(sanPham::setDanhMuc);
        sanPhamService.luu(sanPham);
        redirectAttrs.addFlashAttribute("thanhCong",
                sanPham.getSanPhamId() == null ? "Thêm sản phẩm thành công!" : "Cập nhật sản phẩm thành công!");
        return "redirect:/admin/products";
    }

    /**
     * Ẩn/xóa sản phẩm
     */
    @PostMapping("/products/delete/{id}")
    public String xoaSanPham(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        sanPhamService.xoa(id);
        redirectAttrs.addFlashAttribute("thanhCong", "Đã ẩn sản phẩm!");
        return "redirect:/admin/products";
    }

    // ===================== QUẢN LÝ DANH MỤC =====================

    /**
     * Lưu danh mục
     */
    @PostMapping("/categories/save")
    public String luuDanhMuc(@ModelAttribute DanhMuc danhMuc, RedirectAttributes redirectAttrs) {
        danhMucService.luu(danhMuc);
        redirectAttrs.addFlashAttribute("thanhCong", "Lưu danh mục thành công!");
        return "redirect:/admin/products";
    }

    // ===================== QUẢN LÝ ĐƠN HÀNG =====================

    /**
     * Danh sách đơn hàng
     */
    @GetMapping("/orders")
    public String quanLyDonHang(Model model) {
        model.addAttribute("donHangs", donHangService.layTatCa());
        model.addAttribute("trangThais", TrangThaiDonHang.values());
        return "admin/orders-manage";
    }

    /**
     * Cập nhật trạng thái đơn hàng
     */
    @PostMapping("/orders/{id}/status")
    public String capNhatTrangThai(@PathVariable Long id,
                                    @RequestParam TrangThaiDonHang trangThai,
                                    RedirectAttributes redirectAttrs) {
        donHangService.capNhatTrangThai(id, trangThai);
        redirectAttrs.addFlashAttribute("thanhCong",
                "Cập nhật trạng thái đơn #" + id + " → " + trangThai.getTenHienThi());
        return "redirect:/admin/orders";
    }

    // ===================== QUẢN LÝ NGƯỜI DÙNG =====================

    /**
     * Danh sách người dùng
     */
    @GetMapping("/users")
    public String quanLyNguoiDung(@RequestParam(required = false) String keyword, Model model) {
        var nguoiDungs = (keyword != null && !keyword.isBlank())
                ? nguoiDungService.timKiem(keyword)
                : nguoiDungService.layTatCa();
        model.addAttribute("nguoiDungs", nguoiDungs);
        model.addAttribute("keyword", keyword);
        return "admin/users-manage";
    }

    /**
     * Xóa người dùng
     */
    @PostMapping("/users/delete/{id}")
    public String xoaNguoiDung(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        nguoiDungService.xoa(id);
        redirectAttrs.addFlashAttribute("thanhCong", "Đã xóa người dùng!");
        return "redirect:/admin/users";
    }
}

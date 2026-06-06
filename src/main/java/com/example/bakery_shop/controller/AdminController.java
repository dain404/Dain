package com.example.bakery_shop.controller;

import com.example.bakery_shop.entity.DanhMuc;
import com.example.bakery_shop.entity.DonHang;
import com.example.bakery_shop.entity.NguoiDung;
import com.example.bakery_shop.entity.SanPham;
import com.example.bakery_shop.entity.TrangThaiDonHang;
import com.example.bakery_shop.service.DanhMucService;
import com.example.bakery_shop.service.DonHangService;
import com.example.bakery_shop.service.NguoiDungService;
import com.example.bakery_shop.service.SanPhamService;
import com.example.bakery_shop.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;

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
    private final ExportService exportService;

    @Value("${app.upload.dir}")
    private String uploadDir;

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
        model.addAttribute("donHangMoiNhat", donHangService.layNamDonMoiNhat());

        try {
            ObjectMapper mapper = new ObjectMapper();
            // Data 7 ngày: Object[] { java.sql.Date/LocalDate, BigDecimal }
            model.addAttribute("chartDoanhThu", mapper.writeValueAsString(donHangService.thongKeDoanhThu7NgayQua()));
            // Data top 5 SP: Object[] { String, Long }
            model.addAttribute("chartTopSP", mapper.writeValueAsString(donHangService.thongKeTop5SanPhamBanChay()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "admin/dashboard";
    }

    // ===================== QUẢN LÝ SẢN PHẨM =====================

    /**
     * Danh sách sản phẩm + form thêm mới
     */
    @GetMapping("/products")
    public String quanLySanPham(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<SanPham> sanPhamPage = sanPhamService.layTatCa(page, 10);
        model.addAttribute("sanPhams", sanPhamPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", sanPhamPage.getTotalPages());
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
                              @RequestParam(value = "imageFile", required = false) org.springframework.web.multipart.MultipartFile imageFile,
                              RedirectAttributes redirectAttrs) {
        
        // Xử lý upload ảnh nếu có file mới được tải lên
        if (imageFile != null && !imageFile.isEmpty()) {
            String contentType = imageFile.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/webp"))) {
                redirectAttrs.addFlashAttribute("loi", "Chỉ chấp nhận file ảnh JPG/PNG/WEBP");
                return "redirect:/admin/products";
            }
            if (imageFile.getSize() > 5 * 1024 * 1024) {
                redirectAttrs.addFlashAttribute("loi", "File vượt quá 5MB");
                return "redirect:/admin/products";
            }
            try {
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                
                String extension = "";
                if (contentType.equals("image/jpeg")) extension = ".jpg";
                else if (contentType.equals("image/png")) extension = ".png";
                else if (contentType.equals("image/webp")) extension = ".webp";

                String uniqueFilename = java.util.UUID.randomUUID().toString() + extension;
                java.nio.file.Path filePath = uploadPath.resolve(uniqueFilename);
                java.nio.file.Files.copy(imageFile.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                // Lưu đường dẫn phục vụ tĩnh vào DB
                sanPham.setHinhAnh("/" + uploadDir + "/" + uniqueFilename);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        } else if (sanPham.getSanPhamId() != null) {
            // Trường hợp cập nhật nhưng không tải lên tệp mới, giữ lại ảnh cũ
            sanPhamService.timTheoId(sanPham.getSanPhamId()).ifPresent(oldSp -> {
                sanPham.setHinhAnh(oldSp.getHinhAnh());
            });
        }

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
    public String quanLyDonHang(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<DonHang> donHangPage = donHangService.layTatCa(page, 10);
        model.addAttribute("donHangs", donHangPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", donHangPage.getTotalPages());
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

    /**
     * Xuất báo cáo doanh thu ra Excel
     */
    @GetMapping("/export/revenue")
    public ResponseEntity<byte[]> xuatExcel(@RequestParam int month, @RequestParam int year) {
        byte[] excelContent = exportService.xuatBaoCaoDoanhThuExcel(month, year);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"doanhthu_" + month + "_" + year + ".xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelContent);
    }

    /**
     * Xuất hóa đơn PDF cho đơn hàng
     */
    @GetMapping("/orders/{id}/invoice")
    public ResponseEntity<byte[]> xuatPDF(@PathVariable Long id) {
        byte[] pdfContent = exportService.xuatHoaDonPDF(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"hoadon_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    // ===================== QUẢN LÝ NGƯỜI DÙNG =====================

    /**
     * Danh sách người dùng
     */
    @GetMapping("/users")
    public String quanLyNguoiDung(@RequestParam(required = false) String keyword, 
                                  @RequestParam(defaultValue = "0") int page, 
                                  Model model) {
        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("nguoiDungs", nguoiDungService.timKiem(keyword));
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 1);
        } else {
            Page<NguoiDung> nguoiDungPage = nguoiDungService.layTatCa(page, 10);
            model.addAttribute("nguoiDungs", nguoiDungPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", nguoiDungPage.getTotalPages());
        }
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

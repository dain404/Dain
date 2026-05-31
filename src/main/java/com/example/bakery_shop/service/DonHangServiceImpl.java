package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.*;
import com.example.bakery_shop.repository.ChiTietDonHangRepository;
import com.example.bakery_shop.repository.DonHangRepository;
import com.example.bakery_shop.repository.ThanhToanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Triển khai service đơn hàng
 */
@Service
@RequiredArgsConstructor
public class DonHangServiceImpl implements DonHangService {

    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final SanPhamService sanPhamService;

    @Override
    @Transactional
    public DonHang datHang(DonHang donHang, List<GioHang> gioHangs, String phuongThucThanhToan) {
        // Tính tổng tiền từ giỏ hàng
        BigDecimal tongTien = gioHangs.stream()
                .map(g -> g.getSanPham().getGia().multiply(BigDecimal.valueOf(g.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        donHang.setTongTien(tongTien);
        donHang.setTrangThai(TrangThaiDonHang.CHO_XAC_NHAN);

        // Lưu đơn hàng
        DonHang donHangDaLuu = donHangRepository.save(donHang);

        // Tạo chi tiết đơn hàng và cập nhật tồn kho
        List<ChiTietDonHang> chiTiets = new ArrayList<>();
        for (GioHang gioHang : gioHangs) {
            ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                    .donHang(donHangDaLuu)
                    .sanPham(gioHang.getSanPham())
                    .soLuong(gioHang.getSoLuong())
                    .donGia(gioHang.getSanPham().getGia())
                    .thanhTien(gioHang.getSanPham().getGia()
                            .multiply(BigDecimal.valueOf(gioHang.getSoLuong())))
                    .build();
            chiTiets.add(chiTiet);
            // Trừ tồn kho
            sanPhamService.capNhatTonKho(gioHang.getSanPham().getSanPhamId(), gioHang.getSoLuong());
        }
        chiTietDonHangRepository.saveAll(chiTiets);

        // Tạo bản ghi thanh toán
        ThanhToan thanhToan = ThanhToan.builder()
                .donHang(donHangDaLuu)
                .phuongThucThanhToan(phuongThucThanhToan)
                .trangThaiThanhToan("COD".equals(phuongThucThanhToan) ? "CHO_THANH_TOAN" : "CHO_THANH_TOAN")
                .soTienThanhToan(tongTien)
                .build();
        thanhToanRepository.save(thanhToan);

        return donHangDaLuu;
    }

    @Override
    public List<DonHang> layDonHangCuaUser(Long userId) {
        return donHangRepository.findByNguoiDung_UserIdOrderByNgayDatHangDesc(userId);
    }

    @Override
    public Optional<DonHang> timTheoId(Long donHangId) {
        return donHangRepository.findById(donHangId);
    }

    @Override
    public List<DonHang> layTatCa() {
        return donHangRepository.findAllByOrderByNgayDatHangDesc();
    }

    @Override
    @Transactional
    public DonHang capNhatTrangThai(Long donHangId, TrangThaiDonHang trangThaiMoi) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + donHangId));
        donHang.setTrangThai(trangThaiMoi);
        // Nếu hoàn thành → cập nhật thanh toán
        if (TrangThaiDonHang.HOAN_THANH.equals(trangThaiMoi)) {
            thanhToanRepository.findByDonHang_DonHangId(donHangId).ifPresent(tt -> {
                tt.setTrangThaiThanhToan("DA_THANH_TOAN");
                tt.setNgayThanhToan(LocalDateTime.now());
                thanhToanRepository.save(tt);
            });
        }
        return donHangRepository.save(donHang);
    }

    @Override
    @Transactional
    public void huyDonHang(Long donHangId, Long userId) {
        DonHang donHang = donHangRepository.findById(donHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        // Chỉ cho hủy khi chờ xác nhận và đúng chủ đơn
        if (!donHang.getNguoiDung().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này");
        }
        if (!TrangThaiDonHang.CHO_XAC_NHAN.equals(donHang.getTrangThai())) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đang ở trạng thái 'Chờ xác nhận'");
        }
        donHang.setTrangThai(TrangThaiDonHang.HUY);
        donHangRepository.save(donHang);
    }

    @Override
    public BigDecimal doanhThuThangNay() {
        LocalDateTime dauThang = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime cuoiThang = dauThang.plusMonths(1);
        return donHangRepository.tinhDoanhThu(dauThang, cuoiThang);
    }

    @Override
    public Long demDonTheoTrangThai(TrangThaiDonHang trangThai) {
        return donHangRepository.countByTrangThai(trangThai);
    }
}

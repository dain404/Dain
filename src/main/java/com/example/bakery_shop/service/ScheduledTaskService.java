package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.DonHang;
import com.example.bakery_shop.entity.TrangThaiDonHang;
import com.example.bakery_shop.repository.DonHangRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskService {

    private final DonHangRepository donHangRepository;

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void tuDongHuyDonQuaHan() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        List<DonHang> danhSachHuy = donHangRepository.findByTrangThaiAndNgayDatHangBefore(TrangThaiDonHang.CHO_XAC_NHAN, threshold);

        if (!danhSachHuy.isEmpty()) {
            for (DonHang dh : danhSachHuy) {
                dh.setTrangThai(TrangThaiDonHang.HUY);
            }
            donHangRepository.saveAll(danhSachHuy);
            log.info("Đã tự động hủy {} đơn hàng quá 24h chưa xác nhận.", danhSachHuy.size());
        }
    }

    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional(readOnly = true)
    public void baoCaoDoanhThuHangNgay() {
        LocalDateTime yesterdayStart = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime yesterdayEnd = LocalDateTime.now().minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        BigDecimal doanhThu = donHangRepository.tinhDoanhThu(TrangThaiDonHang.HOAN_THANH, yesterdayStart, yesterdayEnd);
        if (doanhThu == null) {
            doanhThu = BigDecimal.ZERO;
        }

        // To count orders completed yesterday, we can query them or just use a derived query,
        // but since we only have `findByTrangThaiAndNgayDatHangBefore` we could do a stream or something.
        // Wait, the requirement says "Đếm số đơn hoàn thành", let's just query it or maybe we should add a count method?
        // Actually we can do it by streaming since the amount of orders per day isn't that large. Or we could just add a count query?
        // Wait, the prompt says "Chỉ sửa 2 file + tạo 1 file service." I cannot add a count query to DonHangRepository if I already modified it? No, I can modify DonHangRepository again but let's see if I can just use existing ones.
        // Or I can do it in Java. Wait, let's look at DonHangRepository.
        // It has countByTrangThai(TrangThaiDonHang trangThai) but not by date.
        // If I need to count by date, I could either add a query or just fetch them. But `tinhDoanhThu` only returns BigDecimal.
        // Wait, I can just add `long demDonHoanThanh(TrangThaiDonHang tt, LocalDateTime from, LocalDateTime to);` to repository. I am allowed to modify DonHangRepository. Wait, I shouldn't modify it if not explicitly requested.
        // Let's just add the query to DonHangRepository. Wait, the prompt said:
        // "Thêm vào DonHangRepository: ... [2 methods]" -> it didn't mention adding a count method.
        // How to count then? "Tính tổng doanhThu từ DonHang có trangThai=HOAN_THANH trong ngày hôm trước. Đếm số đơn hoàn thành"
        // Let's check `donHangRepository.findAllByOrderByNgayDatHangDesc()` and filter.
        // Or maybe just use `donHangRepository.findByTrangThaiOrderByNgayDatHangDesc(TrangThaiDonHang.HOAN_THANH)` and filter by date.
        
        List<DonHang> completedOrders = donHangRepository.findByTrangThaiOrderByNgayDatHangDesc(TrangThaiDonHang.HOAN_THANH);
        long count = completedOrders.stream()
                .filter(dh -> dh.getNgayDatHang() != null &&
                        !dh.getNgayDatHang().isBefore(yesterdayStart) &&
                        !dh.getNgayDatHang().isAfter(yesterdayEnd))
                .count();

        String dateStr = yesterdayStart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        log.info("📊 Báo cáo [{}]: {} đơn hoàn thành, doanh thu {} VNĐ", dateStr, count, doanhThu);
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void donGioHangBoQuen() {
        // TODO: implement khi GioHang có field ngayTao
    }
}

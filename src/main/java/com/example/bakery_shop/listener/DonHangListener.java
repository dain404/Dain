package com.example.bakery_shop.listener;

import com.example.bakery_shop.config.BeanUtil;
import com.example.bakery_shop.entity.DonHang;
import com.example.bakery_shop.entity.LichSuDonHang;
import com.example.bakery_shop.repository.LichSuDonHangRepository;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

/**
 * JPA Entity Listener tự động ghi lại lịch sử cập nhật trạng thái đơn hàng.
 */
public class DonHangListener {

    @PreUpdate
    public void preUpdate(DonHang donHang) {
        // Chỉ lưu log nếu trạng thái có sự thay đổi
        if (donHang.getTrangThaiCu() != donHang.getTrangThai()) {
            LichSuDonHangRepository repo = BeanUtil.getBean(LichSuDonHangRepository.class);
            
            String nguoiThucHien = "Hệ thống";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String && auth.getPrincipal().equals("anonymousUser"))) {
                Object principal = auth.getPrincipal();
                if (principal instanceof UserDetails) {
                    nguoiThucHien = ((UserDetails) principal).getUsername();
                } else {
                    nguoiThucHien = principal.toString();
                }
            }

            LichSuDonHang log = LichSuDonHang.builder()
                    .donHangId(donHang.getDonHangId())
                    .trangThaiCu(donHang.getTrangThaiCu())
                    .trangThaiMoi(donHang.getTrangThai())
                    .nguoiThucHien(nguoiThucHien)
                    .thoiGian(LocalDateTime.now())
                    .build();

            repo.save(log);
            
            // Cập nhật lại trạng thái cũ sau khi đã lưu log
            donHang.setTrangThaiCu(donHang.getTrangThai());
        }
    }
}

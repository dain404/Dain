package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.DonHang;
import com.example.bakery_shop.entity.TrangThaiDonHang;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    @Async("taskExecutor")
    public void guiEmailXacNhanDonHang(DonHang donHang) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(donHang.getNguoiDung().getEmail());
            helper.setSubject("Xác nhận đơn hàng #" + donHang.getDonHangId());

            NumberFormat vnFormat = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
            String tongTienFormat = vnFormat.format(donHang.getTongTien());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String ngayDat = donHang.getNgayDatHang() != null ? donHang.getNgayDatHang().format(dateFormatter) : "Không xác định";

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto; background-color: #f9f9f9; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);'>"
                    + "<h2 style='color: #4CAF50; text-align: center;'>Cảm ơn bạn đã đặt hàng!</h2>"
                    + "<p style='font-size: 16px; color: #333;'>Xin chào <b>" + donHang.getNguoiDung().getHoTen() + "</b>,</p>"
                    + "<p style='font-size: 16px; color: #333;'>Đơn hàng <b>#" + donHang.getDonHangId() + "</b> của bạn đã được nhận thành công.</p>"
                    + "<table style='width: 100%; border-collapse: collapse; margin-top: 20px; background-color: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.05);'>"
                    + "  <tr style='background-color: #4CAF50; color: white;'>"
                    + "    <th style='padding: 10px; text-align: left;'>Thông tin đơn hàng</th>"
                    + "    <th style='padding: 10px; text-align: right;'>Chi tiết</th>"
                    + "  </tr>"
                    + "  <tr>"
                    + "    <td style='padding: 10px; border-bottom: 1px solid #eee;'>Mã đơn hàng</td>"
                    + "    <td style='padding: 10px; text-align: right; border-bottom: 1px solid #eee;'>#" + donHang.getDonHangId() + "</td>"
                    + "  </tr>"
                    + "  <tr>"
                    + "    <td style='padding: 10px; border-bottom: 1px solid #eee;'>Ngày đặt</td>"
                    + "    <td style='padding: 10px; text-align: right; border-bottom: 1px solid #eee;'>" + ngayDat + "</td>"
                    + "  </tr>"
                    + "  <tr>"
                    + "    <td style='padding: 10px; font-weight: bold;'>Tổng tiền</td>"
                    + "    <td style='padding: 10px; text-align: right; font-weight: bold; color: #E53935;'>" + tongTienFormat + "</td>"
                    + "  </tr>"
                    + "</table>"
                    + "<p style='font-size: 16px; color: #555; margin-top: 20px; text-align: center;'><b>Chúng tôi sẽ liên hệ trong 30 phút.</b></p>"
                    + "<hr style='border: 0; border-top: 1px solid #ddd; margin: 20px 0;'>"
                    + "<p style='font-size: 12px; color: #888; text-align: center;'>Đây là email tự động, vui lòng không phản hồi.</p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            log.info("Đã gửi email xác nhận cho đơn hàng #{}", donHang.getDonHangId());

        } catch (MessagingException | MailException e) {
            log.warn("Không thể gửi email xác nhận cho đơn hàng #{}: {}", donHang.getDonHangId(), e.getMessage());
        }
    }

    @Override
    @Async("taskExecutor")
    public void guiEmailThayDoiTrangThai(DonHang donHang, TrangThaiDonHang trangThaiMoi) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(donHang.getNguoiDung().getEmail());
            helper.setSubject("Cập nhật trạng thái đơn hàng #" + donHang.getDonHangId());

            String tenTrangThai = trangThaiMoi.getTenHienThi();

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto; background-color: #f9f9f9; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);'>"
                    + "<h2 style='color: #2196F3; text-align: center;'>Cập nhật đơn hàng</h2>"
                    + "<p style='font-size: 16px; color: #333;'>Xin chào <b>" + donHang.getNguoiDung().getHoTen() + "</b>,</p>"
                    + "<p style='font-size: 16px; color: #333;'>Đơn hàng <b>#" + donHang.getDonHangId() + "</b> của bạn đã được cập nhật trạng thái mới.</p>"
                    + "<div style='background-color: #E3F2FD; padding: 15px; border-radius: 8px; text-align: center; margin: 20px 0;'>"
                    + "  <span style='font-size: 18px; color: #1565C0;'>Trạng thái hiện tại: </span>"
                    + "  <span style='font-size: 20px; font-weight: bold; color: #0D47A1; text-transform: uppercase;'>" + tenTrangThai + "</span>"
                    + "</div>"
                    + "<p style='font-size: 16px; color: #555; text-align: center;'>Cảm ơn bạn đã đồng hành cùng chúng tôi!</p>"
                    + "<hr style='border: 0; border-top: 1px solid #ddd; margin: 20px 0;'>"
                    + "<p style='font-size: 12px; color: #888; text-align: center;'>Đây là email tự động, vui lòng không phản hồi.</p>"
                    + "</div>";

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            log.info("Đã gửi email cập nhật trạng thái cho đơn hàng #{}", donHang.getDonHangId());

        } catch (MessagingException | MailException e) {
            log.warn("Không thể gửi email cập nhật trạng thái cho đơn hàng #{}: {}", donHang.getDonHangId(), e.getMessage());
        }
    }
}

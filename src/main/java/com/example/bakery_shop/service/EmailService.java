package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.DonHang;
import com.example.bakery_shop.entity.TrangThaiDonHang;

public interface EmailService {
    void guiEmailXacNhanDonHang(DonHang donHang);
    void guiEmailThayDoiTrangThai(DonHang donHang, TrangThaiDonHang trangThaiMoi);
}

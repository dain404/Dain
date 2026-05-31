package com.example.bakery_shop.entity;

/**
 * Enum trạng thái đơn hàng theo luồng nghiệp vụ AGENTS.md
 */
public enum TrangThaiDonHang {
    CHO_XAC_NHAN("Chờ xác nhận"),
    DA_XAC_NHAN("Đã xác nhận"),
    DANG_CHUAN_BI("Đang chuẩn bị"),
    DANG_GIAO("Đang giao"),
    HOAN_THANH("Hoàn thành"),
    HUY("Đã hủy"),
    HOAN_TIEN("Hoàn tiền");

    private final String tenHienThi;

    TrangThaiDonHang(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}

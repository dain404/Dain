package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.ChiTietDonHang;
import com.example.bakery_shop.entity.DonHang;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final DonHangService donHangService;

    public byte[] xuatBaoCaoDoanhThuExcel(int month, int year) {
        List<DonHang> tatCa = donHangService.layTatCa();
        List<DonHang> danhSach = tatCa.stream()
                .filter(dh -> dh.getNgayDatHang() != null 
                        && dh.getNgayDatHang().getMonthValue() == month 
                        && dh.getNgayDatHang().getYear() == year)
                .collect(Collectors.toList());

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Doanh Thu " + month + "-" + year);

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);

            // Row 0: Header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"STT", "Mã ĐH", "Khách hàng", "Ngày đặt", "Tổng tiền", "Trạng thái"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            int rowIdx = 1;
            BigDecimal totalSum = BigDecimal.ZERO;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (DonHang dh : danhSach) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue("#" + dh.getDonHangId());
                row.createCell(2).setCellValue(dh.getNguoiDung() != null ? dh.getNguoiDung().getHoTen() : "");
                row.createCell(3).setCellValue(dh.getNgayDatHang() != null ? dh.getNgayDatHang().format(formatter) : "");
                
                Cell cellTongTien = row.createCell(4);
                cellTongTien.setCellValue(dh.getTongTien().doubleValue());
                
                row.createCell(5).setCellValue(dh.getTrangThai().getTenHienThi());
                
                if (dh.getTrangThai() == com.example.bakery_shop.entity.TrangThaiDonHang.HOAN_THANH) {
                    totalSum = totalSum.add(dh.getTongTien());
                }
            }

            // Dòng cuối: tổng cộng
            Row totalRow = sheet.createRow(rowIdx);
            CellStyle totalStyle = workbook.createCellStyle();
            totalStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);

            Cell labelCell = totalRow.createCell(3);
            labelCell.setCellValue("Tổng cộng (Chỉ tính đơn Hoàn thành):");
            labelCell.setCellStyle(totalStyle);

            Cell sumCell = totalRow.createCell(4);
            sumCell.setCellValue(totalSum.doubleValue());
            sumCell.setCellStyle(totalStyle);

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xuất file Excel", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] xuatHoaDonPDF(Long donHangId) {
        DonHang donHang = donHangService.timTheoId(donHangId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Font (Sử dụng Helvetica chuẩn của OpenPDF)
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            // Header
            Paragraph title = new Paragraph("HOA DON MUA HANG - BAKERY SHOP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Info
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            document.add(new Paragraph("Ma don hang: #" + donHang.getDonHangId(), normalFont));
            document.add(new Paragraph("Ngay dat: " + (donHang.getNgayDatHang() != null ? donHang.getNgayDatHang().format(formatter) : ""), normalFont));
            document.add(new Paragraph("Khach hang: " + (donHang.getNguoiDung() != null ? donHang.getNguoiDung().getHoTen() : ""), normalFont));
            document.add(new Paragraph("Dia chi: " + donHang.getDiaChiGiaoHang(), normalFont));
            document.add(new Paragraph(" "));

            // Bảng chi tiết
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 1f, 2f, 2f});

            // Table Header
            String[] headers = {"San pham", "SL", "Don gia", "Thanh tien"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, boldFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Table Data
            NumberFormat vnFormat = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
            if (donHang.getChiTietDonHangs() != null) {
                for (ChiTietDonHang ct : donHang.getChiTietDonHangs()) {
                    table.addCell(new Phrase(ct.getSanPham().getTenSanPham(), normalFont));
                    
                    PdfPCell slCell = new PdfPCell(new Phrase(String.valueOf(ct.getSoLuong()), normalFont));
                    slCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(slCell);

                    PdfPCell giaCell = new PdfPCell(new Phrase(vnFormat.format(ct.getDonGia()), normalFont));
                    giaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(giaCell);

                    PdfPCell thanhTienCell = new PdfPCell(new Phrase(vnFormat.format(ct.getThanhTien()), normalFont));
                    thanhTienCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(thanhTienCell);
                }
            }
            document.add(table);
            document.add(new Paragraph(" "));

            // Footer
            Paragraph tongTien = new Paragraph("Tong tien: " + vnFormat.format(donHang.getTongTien()), boldFont);
            tongTien.setAlignment(Element.ALIGN_RIGHT);
            document.add(tongTien);

            Paragraph footer = new Paragraph("Cam on quy khach!", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12));
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xuất file PDF", e);
        }
    }
}

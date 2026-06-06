package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.*;
import com.example.bakery_shop.repository.ChiTietDonHangRepository;
import com.example.bakery_shop.repository.DonHangRepository;
import com.example.bakery_shop.repository.SanPhamRepository;
import com.example.bakery_shop.repository.ThanhToanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DonHangServiceImplTest {

    @Mock
    private DonHangRepository donHangRepository;

    @Mock
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Mock
    private ThanhToanRepository thanhToanRepository;

    @Mock
    private SanPhamService sanPhamService;

    @Mock
    private EmailService emailService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private SanPhamRepository sanPhamRepository;

    @InjectMocks
    private DonHangServiceImpl donHangService;

    @Captor
    private ArgumentCaptor<ThanhToan> thanhToanCaptor;

    @Captor
    private ArgumentCaptor<DonHang> donHangCaptor;

    // ==========================================
    // GROUP A: datHang()
    // ==========================================

    @Test
    void datHang_haiSanPham_tinhTongTienDung() {
        // Mock SanPham
        SanPham banhA = SanPham.builder().sanPhamId(1L).gia(BigDecimal.valueOf(50000)).tenSanPham("Bánh A").build();
        SanPham banhB = SanPham.builder().sanPhamId(2L).gia(BigDecimal.valueOf(30000)).tenSanPham("Bánh B").build();

        // Mock GioHang
        GioHang gh1 = Mockito.mock(GioHang.class);
        when(gh1.getSanPham()).thenReturn(banhA);
        when(gh1.getSoLuong()).thenReturn(2);

        GioHang gh2 = Mockito.mock(GioHang.class);
        when(gh2.getSanPham()).thenReturn(banhB);
        when(gh2.getSoLuong()).thenReturn(3);

        List<GioHang> gioHangs = List.of(gh1, gh2);
        DonHang donHangInput = new DonHang();

        // Mock Save
        DonHang donHangMock = DonHang.builder().donHangId(1L).tongTien(BigDecimal.valueOf(190000)).build();
        when(donHangRepository.save(any(DonHang.class))).thenReturn(donHangMock);

        // Act
        DonHang result = donHangService.datHang(donHangInput, gioHangs, "COD");

        // Assert
        assertEquals(BigDecimal.valueOf(190000), donHangInput.getTongTien());
        assertEquals(1L, result.getDonHangId());
    }

    @Test
    void datHang_gioHangRong_tongTienBangZero() {
        List<GioHang> gioHangs = new ArrayList<>();
        DonHang donHangInput = new DonHang();

        when(donHangRepository.save(any(DonHang.class))).thenAnswer(i -> i.getArgument(0));

        donHangService.datHang(donHangInput, gioHangs, "COD");

        assertEquals(BigDecimal.ZERO, donHangInput.getTongTien());
    }

    @Test
    void datHang_luuChiTietDonHangDungSoLuong() {
        SanPham banhA = SanPham.builder().sanPhamId(1L).gia(BigDecimal.valueOf(10000)).build();
        
        GioHang gh1 = Mockito.mock(GioHang.class);
        when(gh1.getSanPham()).thenReturn(banhA);
        when(gh1.getSoLuong()).thenReturn(1);

        GioHang gh2 = Mockito.mock(GioHang.class);
        when(gh2.getSanPham()).thenReturn(banhA);
        when(gh2.getSoLuong()).thenReturn(1);

        GioHang gh3 = Mockito.mock(GioHang.class);
        when(gh3.getSanPham()).thenReturn(banhA);
        when(gh3.getSoLuong()).thenReturn(1);

        List<GioHang> gioHangs = List.of(gh1, gh2, gh3);
        DonHang donHangInput = new DonHang();
        when(donHangRepository.save(any(DonHang.class))).thenReturn(donHangInput);

        donHangService.datHang(donHangInput, gioHangs, "COD");

        verify(chiTietDonHangRepository, times(1)).saveAll(argThat(list -> ((List<?>) list).size() == 3));
    }

    @Test
    void datHang_capNhatTonKhoChoMoiSanPham() {
        SanPham banhA = SanPham.builder().sanPhamId(11L).gia(BigDecimal.valueOf(10000)).build();
        SanPham banhB = SanPham.builder().sanPhamId(22L).gia(BigDecimal.valueOf(20000)).build();
        SanPham banhC = SanPham.builder().sanPhamId(33L).gia(BigDecimal.valueOf(30000)).build();

        GioHang gh1 = Mockito.mock(GioHang.class);
        when(gh1.getSanPham()).thenReturn(banhA);
        when(gh1.getSoLuong()).thenReturn(1);

        GioHang gh2 = Mockito.mock(GioHang.class);
        when(gh2.getSanPham()).thenReturn(banhB);
        when(gh2.getSoLuong()).thenReturn(2);

        GioHang gh3 = Mockito.mock(GioHang.class);
        when(gh3.getSanPham()).thenReturn(banhC);
        when(gh3.getSoLuong()).thenReturn(3);

        List<GioHang> gioHangs = List.of(gh1, gh2, gh3);
        DonHang donHangInput = new DonHang();
        when(donHangRepository.save(any(DonHang.class))).thenReturn(donHangInput);

        donHangService.datHang(donHangInput, gioHangs, "COD");

        verify(sanPhamService, times(3)).capNhatTonKho(anyLong(), anyInt());
        verify(sanPhamService).capNhatTonKho(11L, 1);
        verify(sanPhamService).capNhatTonKho(22L, 2);
        verify(sanPhamService).capNhatTonKho(33L, 3);
    }

    @Test
    void datHang_luuThanhToanVoiSoTienDung() {
        SanPham banhA = SanPham.builder().sanPhamId(1L).gia(BigDecimal.valueOf(75000)).build();
        GioHang gh1 = Mockito.mock(GioHang.class);
        when(gh1.getSanPham()).thenReturn(banhA);
        when(gh1.getSoLuong()).thenReturn(2);

        DonHang donHangInput = new DonHang();
        when(donHangRepository.save(any(DonHang.class))).thenReturn(donHangInput);

        donHangService.datHang(donHangInput, List.of(gh1), "COD");

        verify(thanhToanRepository).save(thanhToanCaptor.capture());
        ThanhToan thanhToan = thanhToanCaptor.getValue();

        assertEquals(BigDecimal.valueOf(150000), thanhToan.getSoTienThanhToan());
    }

    @Test
    void datHang_trangThaiInitialLaCHO_XAC_NHAN() {
        DonHang donHangInput = new DonHang();
        when(donHangRepository.save(any(DonHang.class))).thenReturn(donHangInput);

        donHangService.datHang(donHangInput, Collections.emptyList(), "COD");

        verify(donHangRepository).save(donHangCaptor.capture());
        assertEquals(TrangThaiDonHang.CHO_XAC_NHAN, donHangCaptor.getValue().getTrangThai());
    }

    // ==========================================
    // GROUP B: huyDonHang()
    // ==========================================

    @Test
    void huyDonHang_dongNguoiDungKhacNemException() {
        NguoiDung owner = NguoiDung.builder().userId(1L).build();
        DonHang donHang = DonHang.builder().donHangId(100L).nguoiDung(owner).build();
        
        when(donHangRepository.findById(100L)).thenReturn(Optional.of(donHang));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            donHangService.huyDonHang(100L, 99L);
        });
        assertTrue(exception.getMessage().toLowerCase().contains("quyền"));
    }

    @Test
    void huyDonHang_trangThaiKhongPhaiCHO_XAC_NHAN_nemException() {
        NguoiDung owner = NguoiDung.builder().userId(1L).build();
        DonHang donHang = DonHang.builder()
                .donHangId(100L)
                .nguoiDung(owner)
                .trangThai(TrangThaiDonHang.DANG_GIAO)
                .build();
        
        when(donHangRepository.findById(100L)).thenReturn(Optional.of(donHang));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            donHangService.huyDonHang(100L, 1L);
        });
        assertTrue(exception.getMessage().toLowerCase().contains("không thể hủy"));
    }

    @Test
    void huyDonHang_hopLe_setTrangThaiHUY() {
        NguoiDung owner = NguoiDung.builder().userId(1L).build();
        DonHang donHang = DonHang.builder()
                .donHangId(100L)
                .nguoiDung(owner)
                .trangThai(TrangThaiDonHang.CHO_XAC_NHAN)
                .build();
        
        SanPham sp = SanPham.builder().sanPhamId(1L).soLuongTon(10).build();
        ChiTietDonHang ct = ChiTietDonHang.builder().sanPham(sp).soLuong(2).build();

        when(donHangRepository.findById(100L)).thenReturn(Optional.of(donHang));
        when(chiTietDonHangRepository.findByDonHangIdFetchSanPham(100L)).thenReturn(List.of(ct));

        donHangService.huyDonHang(100L, 1L);

        verify(sanPhamRepository).save(sp);
        assertEquals(12, sp.getSoLuongTon());
        verify(donHangRepository).save(argThat(d -> d.getTrangThai() == TrangThaiDonHang.HUY));
    }

    @Test
    void huyDonHang_khongTimThayDon_nemException() {
        when(donHangRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            donHangService.huyDonHang(99L, 1L);
        });
        assertTrue(exception.getMessage().toLowerCase().contains("không tìm thấy"));
    }

    // ==========================================
    // GROUP C: capNhatTrangThai()
    // ==========================================

    @Test
    void capNhatTrangThai_thanhHOAN_THANH_capNhatThanhToan() {
        DonHang donHang = DonHang.builder().donHangId(1L).trangThai(TrangThaiDonHang.CHO_XAC_NHAN).build();
        ThanhToan thanhToan = ThanhToan.builder().trangThaiThanhToan("CHO_THANH_TOAN").build();

        when(donHangRepository.findById(1L)).thenReturn(Optional.of(donHang));
        when(thanhToanRepository.findByDonHang_DonHangId(1L)).thenReturn(Optional.of(thanhToan));
        when(donHangRepository.save(any(DonHang.class))).thenReturn(donHang);

        donHangService.capNhatTrangThai(1L, TrangThaiDonHang.HOAN_THANH);

        verify(thanhToanRepository).save(thanhToanCaptor.capture());
        ThanhToan savedThanhToan = thanhToanCaptor.getValue();
        
        assertEquals("DA_THANH_TOAN", savedThanhToan.getTrangThaiThanhToan());
        assertNotNull(savedThanhToan.getNgayThanhToan());
    }

    @Test
    void capNhatTrangThai_khongPhaiHOAN_THANH_khongChinhThanhToan() {
        DonHang donHang = DonHang.builder().donHangId(1L).trangThai(TrangThaiDonHang.CHO_XAC_NHAN).build();

        when(donHangRepository.findById(1L)).thenReturn(Optional.of(donHang));
        when(donHangRepository.save(any(DonHang.class))).thenReturn(donHang);

        donHangService.capNhatTrangThai(1L, TrangThaiDonHang.DANG_GIAO);

        verify(thanhToanRepository, never()).save(any());
        verify(thanhToanRepository, never()).findByDonHang_DonHangId(anyLong());
    }
}

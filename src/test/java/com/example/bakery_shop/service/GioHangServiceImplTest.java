package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.GioHang;
import com.example.bakery_shop.entity.NguoiDung;
import com.example.bakery_shop.entity.SanPham;
import com.example.bakery_shop.repository.GioHangRepository;
import com.example.bakery_shop.repository.NguoiDungRepository;
import com.example.bakery_shop.repository.SanPhamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GioHangServiceImplTest {

    @Mock
    private GioHangRepository gioHangRepository;

    @Mock
    private NguoiDungRepository nguoiDungRepository;

    @Mock
    private SanPhamRepository sanPhamRepository;

    @InjectMocks
    private GioHangServiceImpl gioHangService;

    @Captor
    private ArgumentCaptor<GioHang> gioHangCaptor;

    // ==========================================
    // themVaoGio()
    // ==========================================

    @Test
    void themVaoGio_sanPhamDaTonTai_tangSoLuong() {
        SanPham sp = SanPham.builder().sanPhamId(1L).trangThai(true).soLuongTon(100).build();
        when(sanPhamRepository.findById(1L)).thenReturn(Optional.of(sp));

        GioHang existing = GioHang.builder().gioHangId(1L).soLuong(2).build();
        when(gioHangRepository.findByNguoiDung_UserIdAndSanPham_SanPhamId(1L, 1L)).thenReturn(Optional.of(existing));
        when(gioHangRepository.save(any(GioHang.class))).thenAnswer(i -> i.getArgument(0));

        gioHangService.themVaoGio(1L, 1L, 3);

        verify(gioHangRepository).save(gioHangCaptor.capture());
        assertEquals(5, gioHangCaptor.getValue().getSoLuong());
    }

    @Test
    void themVaoGio_sanPhamMoi_taoGioHangMoi() {
        SanPham sp = SanPham.builder().sanPhamId(1L).trangThai(true).soLuongTon(100).build();
        when(sanPhamRepository.findById(1L)).thenReturn(Optional.of(sp));
        when(gioHangRepository.findByNguoiDung_UserIdAndSanPham_SanPhamId(1L, 1L)).thenReturn(Optional.empty());
        
        NguoiDung nd = NguoiDung.builder().userId(1L).build();
        when(nguoiDungRepository.findById(1L)).thenReturn(Optional.of(nd));
        when(gioHangRepository.save(any(GioHang.class))).thenAnswer(i -> i.getArgument(0));

        gioHangService.themVaoGio(1L, 1L, 2);

        verify(gioHangRepository).save(argThat(gh -> gh.getSoLuong() == 2 && gh.getNguoiDung().getUserId() == 1L && gh.getSanPham().getSanPhamId() == 1L));
    }

    @Test
    void themVaoGio_khongTimThayNguoiDung_nemException() {
        SanPham sp = SanPham.builder().sanPhamId(1L).trangThai(true).soLuongTon(100).build();
        when(sanPhamRepository.findById(1L)).thenReturn(Optional.of(sp));
        when(gioHangRepository.findByNguoiDung_UserIdAndSanPham_SanPhamId(1L, 1L)).thenReturn(Optional.empty());
        when(nguoiDungRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gioHangService.themVaoGio(1L, 1L, 2);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("người dùng"));
    }

    // ==========================================
    // capNhatSoLuong()
    // ==========================================

    @Test
    void capNhatSoLuong_soLuongMoiLaZero_xoaVaTraVeNull() {
        GioHang existing = GioHang.builder().gioHangId(1L).soLuong(2).build();
        when(gioHangRepository.findById(1L)).thenReturn(Optional.of(existing));

        GioHang result = gioHangService.capNhatSoLuong(1L, 0);

        verify(gioHangRepository).delete(existing);
        assertNull(result);
    }

    @Test
    void capNhatSoLuong_soLuongAm_xoaVaTraVeNull() {
        GioHang existing = GioHang.builder().gioHangId(1L).soLuong(2).build();
        when(gioHangRepository.findById(1L)).thenReturn(Optional.of(existing));

        GioHang result = gioHangService.capNhatSoLuong(1L, -5);

        verify(gioHangRepository).delete(existing);
        assertNull(result);
    }

    @Test
    void capNhatSoLuong_soLuongHopLe_capNhatVaTraVe() {
        GioHang existing = GioHang.builder().gioHangId(1L).soLuong(2).build();
        when(gioHangRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(gioHangRepository.save(any(GioHang.class))).thenAnswer(i -> i.getArgument(0));

        GioHang result = gioHangService.capNhatSoLuong(1L, 4);

        verify(gioHangRepository).save(gioHangCaptor.capture());
        assertEquals(4, gioHangCaptor.getValue().getSoLuong());
        assertNotNull(result);
    }

    // ==========================================
    // xoaGioHang()
    // ==========================================

    @Test
    void xoaGioHang_goiRepositoryDungUserId() {
        gioHangService.xoaGioHang(42L);

        verify(gioHangRepository, times(1)).xoaGioHangCuaNguoiDung(42L);
    }
}

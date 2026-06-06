package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.SanPham;
import com.example.bakery_shop.repository.SanPhamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SanPhamServiceImplTest {

    @Mock
    private SanPhamRepository sanPhamRepository;

    @InjectMocks
    private SanPhamServiceImpl sanPhamService;

    @Captor
    private ArgumentCaptor<SanPham> sanPhamCaptor;

    // ==========================================
    // capNhatTonKho()
    // ==========================================

    @Test
    void capNhatTonKho_tonKhoVuaDu_truDung() {
        SanPham sp = SanPham.builder().sanPhamId(1L).soLuongTon(10).build();
        when(sanPhamRepository.findById(1L)).thenReturn(Optional.of(sp));
        when(sanPhamRepository.save(any(SanPham.class))).thenAnswer(i -> i.getArgument(0));

        sanPhamService.capNhatTonKho(1L, 3);

        verify(sanPhamRepository).save(sanPhamCaptor.capture());
        assertEquals(7, sanPhamCaptor.getValue().getSoLuongTon());
    }

    @Test
    void capNhatTonKho_tonKhoVua_truVeZero() {
        SanPham sp = SanPham.builder().sanPhamId(1L).soLuongTon(5).build();
        when(sanPhamRepository.findById(1L)).thenReturn(Optional.of(sp));
        when(sanPhamRepository.save(any(SanPham.class))).thenAnswer(i -> i.getArgument(0));

        sanPhamService.capNhatTonKho(1L, 5);

        verify(sanPhamRepository).save(sanPhamCaptor.capture());
        assertEquals(0, sanPhamCaptor.getValue().getSoLuongTon());
    }

    @Test
    void capNhatTonKho_khongDuTonKho_nemException() {
        SanPham sp = SanPham.builder().sanPhamId(1L).soLuongTon(2).tenSanPham("Bánh test").build();
        when(sanPhamRepository.findById(1L)).thenReturn(Optional.of(sp));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sanPhamService.capNhatTonKho(1L, 5);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("không đủ tồn kho"));
        verify(sanPhamRepository, never()).save(any());
    }

    @Test
    void capNhatTonKho_khongTimThayMaSanPham_nemException() {
        when(sanPhamRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sanPhamService.capNhatTonKho(99L, 1);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("không tìm thấy sản phẩm"));
    }

    // ==========================================
    // xoa()
    // ==========================================

    @Test
    void xoa_anMemKhongXoaCung() {
        SanPham sp = SanPham.builder().sanPhamId(1L).trangThai(true).build();
        when(sanPhamRepository.findById(1L)).thenReturn(Optional.of(sp));
        when(sanPhamRepository.save(any(SanPham.class))).thenAnswer(i -> i.getArgument(0));

        sanPhamService.xoa(1L);

        verify(sanPhamRepository).save(sanPhamCaptor.capture());
        assertFalse(sanPhamCaptor.getValue().getTrangThai());
        verify(sanPhamRepository, never()).deleteById(anyLong());
    }

    @Test
    void xoa_khongTimThay_nemException() {
        when(sanPhamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            sanPhamService.xoa(99L);
        });
    }

    // ==========================================
    // luu()
    // ==========================================

    @Test
    void luu_goiSaveVaTraVeKetQua() {
        SanPham input = SanPham.builder().tenSanPham("Bánh mới").build();
        SanPham saved = SanPham.builder().sanPhamId(99L).tenSanPham("Bánh mới").build();
        
        when(sanPhamRepository.save(input)).thenReturn(saved);

        SanPham result = sanPhamService.luu(input);

        assertEquals(99L, result.getSanPhamId());
        verify(sanPhamRepository, times(1)).save(input);
    }
}

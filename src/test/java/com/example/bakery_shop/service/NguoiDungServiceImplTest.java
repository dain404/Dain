package com.example.bakery_shop.service;

import com.example.bakery_shop.entity.NguoiDung;
import com.example.bakery_shop.repository.NguoiDungRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NguoiDungServiceImplTest {

    @Mock
    private NguoiDungRepository nguoiDungRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private NguoiDungServiceImpl nguoiDungService;

    @Captor
    private ArgumentCaptor<NguoiDung> nguoiDungCaptor;

    // ==========================================
    // dangKy()
    // ==========================================

    @Test
    void dangKy_emailTrung_nemRuntimeException() {
        when(nguoiDungRepository.existsByEmail("trung@test.com")).thenReturn(true);

        NguoiDung nguoiDung = NguoiDung.builder().email("trung@test.com").build();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            nguoiDungService.dangKy(nguoiDung);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("email"));
        verify(nguoiDungRepository, never()).save(any());
    }

    @Test
    void dangKy_emailMoi_maHoaMatKhau() {
        when(nguoiDungRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("raw123")).thenReturn("hashed_abc");
        when(nguoiDungRepository.save(any(NguoiDung.class))).thenAnswer(i -> i.getArgument(0));

        NguoiDung nguoiDung = NguoiDung.builder().email("new@test.com").matKhau("raw123").build();
        nguoiDungService.dangKy(nguoiDung);

        verify(nguoiDungRepository).save(nguoiDungCaptor.capture());
        assertEquals("hashed_abc", nguoiDungCaptor.getValue().getMatKhau());
    }

    @Test
    void dangKy_vaiTroNull_ganROLE_USER() {
        when(nguoiDungRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("pwd");
        
        NguoiDung nguoiDung = NguoiDung.builder().email("user@test.com").matKhau("123").vaiTro(null).build();
        nguoiDungService.dangKy(nguoiDung);

        verify(nguoiDungRepository).save(nguoiDungCaptor.capture());
        assertEquals("ROLE_USER", nguoiDungCaptor.getValue().getVaiTro());
    }

    @Test
    void dangKy_vaiTroDaCoSan_giuNguyenVaiTro() {
        when(nguoiDungRepository.existsByEmail("admin@test.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("pwd");
        
        NguoiDung nguoiDung = NguoiDung.builder().email("admin@test.com").matKhau("123").vaiTro("ROLE_ADMIN").build();
        nguoiDungService.dangKy(nguoiDung);

        verify(nguoiDungRepository).save(nguoiDungCaptor.capture());
        assertEquals("ROLE_ADMIN", nguoiDungCaptor.getValue().getVaiTro());
    }

    // ==========================================
    // doiMatKhau()
    // ==========================================

    @Test
    void doiMatKhau_matKhauCuSai_nemException() {
        NguoiDung existing = NguoiDung.builder().userId(1L).matKhau("hashed").build();
        when(nguoiDungRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("sai", "hashed")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            nguoiDungService.doiMatKhau(1L, "sai", "new");
        });

        assertTrue(exception.getMessage().toLowerCase().contains("mật khẩu cũ"));
    }

    @Test
    void doiMatKhau_hopLe_luuMatKhauMoiDaMaHoa() {
        NguoiDung existing = NguoiDung.builder().userId(1L).matKhau("hashed").build();
        when(nguoiDungRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("dung", "hashed")).thenReturn(true);
        when(passwordEncoder.encode("matKhauMoi")).thenReturn("new_hash");

        nguoiDungService.doiMatKhau(1L, "dung", "matKhauMoi");

        verify(nguoiDungRepository).save(nguoiDungCaptor.capture());
        assertEquals("new_hash", nguoiDungCaptor.getValue().getMatKhau());
    }

    @Test
    void doiMatKhau_khongTimThayUser_nemException() {
        when(nguoiDungRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            nguoiDungService.doiMatKhau(99L, "old", "new");
        });
    }

    // ==========================================
    // capNhatThongTin()
    // ==========================================

    @Test
    void capNhatThongTin_khongDucMatKhauVaEmail() {
        NguoiDung existing = NguoiDung.builder()
                .userId(1L)
                .matKhau("secret")
                .email("old@test.com")
                .hoTen("Tên Cũ")
                .build();
                
        NguoiDung updateData = NguoiDung.builder()
                .userId(1L)
                .matKhau("newpwd")
                .email("new@test.com")
                .hoTen("Tên Mới")
                .build();

        when(nguoiDungRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(nguoiDungRepository.save(any(NguoiDung.class))).thenAnswer(i -> i.getArgument(0));

        nguoiDungService.capNhatThongTin(updateData);

        verify(nguoiDungRepository).save(nguoiDungCaptor.capture());
        NguoiDung saved = nguoiDungCaptor.getValue();
        
        assertEquals("secret", saved.getMatKhau());
        assertEquals("old@test.com", saved.getEmail());
        assertEquals("Tên Mới", saved.getHoTen());
    }
}

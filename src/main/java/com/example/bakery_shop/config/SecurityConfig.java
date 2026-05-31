package com.example.bakery_shop.config;

import com.example.bakery_shop.entity.NguoiDung;
import com.example.bakery_shop.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import java.util.List;

/**
 * Cấu hình Spring Security.
 * Implements UserDetailsService để load người dùng từ NguoiDungRepository theo email.
 * Không dùng InMemoryUserDetailsManager.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig implements UserDetailsService {

    // Inject trực tiếp repository để load user theo email
    private final NguoiDungRepository nguoiDungRepository;

    /**
     * Load UserDetails từ database theo email (username = email trong hệ thống này).
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy tài khoản với email: " + email));

        // Gán quyền từ trường vaiTro (ROLE_USER / ROLE_ADMIN)
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(nguoiDung.getVaiTro())
        );

        return new User(nguoiDung.getEmail(), nguoiDung.getMatKhau(), authorities);
    }

    /**
     * Bean mã hóa mật khẩu BCrypt — dùng trong toàn bộ ứng dụng.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cấu hình Security Filter Chain:
     * - Public: trang xem sản phẩm, đăng nhập, đăng ký, static resources
     * - ROLE_USER: giỏ hàng, đặt hàng, lịch sử, profile
     * - ROLE_ADMIN: /admin/**
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Tài nguyên tĩnh và trang công khai
                .requestMatchers(
                    "/",
                    "/about",
                    "/products",
                    "/products/**",
                    "/login",
                    "/register",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**",
                    "/ws/**"
                ).permitAll()
                // Chỉ admin mới truy cập được /admin/**
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Các trang cần đăng nhập
                .requestMatchers(
                    "/cart/**",
                    "/orders/**",
                    "/profile/**",
                    "/checkout/**"
                ).hasAnyRole("USER", "ADMIN")
                // Mọi request khác cần xác thực
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")                    // Trang đăng nhập tùy chỉnh
                .loginProcessingUrl("/login")           // URL xử lý POST login
                .usernameParameter("email")             // Dùng email làm username
                .passwordParameter("matKhau")           // Tên field mật khẩu
                .defaultSuccessUrl("/", true)           // Sau đăng nhập → trang chủ
                .failureUrl("/login?error=true")        // Sai tài khoản → báo lỗi
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // Bật CSRF cho form thông thường (disable cho API nếu cần)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/ws/**")
            );

        return http.build();
    }
}

package com.tailieuptit.demo.config;

import com.tailieuptit.demo.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Bật @PreAuthorize trên Controller/Service
public class SecurityConfig {

    /**
     * Bean này định nghĩa phương thức mã hóa mật khẩu.
     * Chúng ta dùng BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean này chỉ cho Spring Security biết cách lấy thông tin User
     * và cách kiểm tra mật khẩu.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserService userService,
                                                         PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Cần thiết cho một số quy trình xác thực (tùy chọn)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Đây là nơi cấu hình chính, định nghĩa các quy tắc bảo vệ.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationProvider authenticationProvider) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Vô hiệu hóa CSRF (API không dùng Session)
                .authorizeHttpRequests(auth -> auth
                        // --- ADMIN ---
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Chỉ ADMIN

                        // --- USER ACTIONS (Cần đăng nhập) ---
                        // (Chúng ta sẽ dùng @PreAuthorize, nhưng có thể khóa cứng ở đây)
                        .requestMatchers(
                                "/api/documents/upload",
                                "/api/documents/download/**",
                                "/api/documents/{id}/comment",
                                "/api/documents/{id}/rate",
                                "/api/users/me",
                                "/profile",
                                "/document/upload"
                        ).authenticated() // Yêu cầu BẤT KỲ VAI TRÒ nào (USER hoặc ADMIN)

                        // --- PUBLIC (Cho phép tất cả) ---
                        .requestMatchers(
                                "/",
                                "/auth/**",               // Trang login/register (HTML)
                                "/api/auth/**",           // API đăng ký
                                "/api/documents/**",      // API xem danh sách, chi tiết
                                "/api/categories",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**",             // Thư mục file upload
                                "/favicon.ico"
                        ).permitAll()

                        // --- MỌI THỨ KHÁC ---
                        .anyRequest().authenticated() // Tất cả các yêu cầu còn lại phải đăng nhập
                )

                // Cấu hình Form Đăng nhập
                .formLogin(form -> form
                        .loginPage("/auth/login")         // GET: Đường dẫn đến trang login.html
                        .loginProcessingUrl("/auth/login") // POST: URL mà form login sẽ gửi tới
                        .defaultSuccessUrl("/", true)     // Về trang chủ sau khi login
                        .failureUrl("/auth/login?error=true") // Về trang login nếu lỗi
                        .permitAll() // Cho phép tất cả truy cập trang login
                )

                // Cấu hình Đăng xuất
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")         // URL để kích hoạt logout
                        .logoutSuccessUrl("/auth/login?logout=true") // Về trang login
                        .permitAll()
                )

                // Chỉ định Provider xác thực của chúng ta
                .authenticationProvider(authenticationProvider);

        return http.build();
    }

}
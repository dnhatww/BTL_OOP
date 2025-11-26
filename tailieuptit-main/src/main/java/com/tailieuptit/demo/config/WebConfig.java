package com.tailieuptit.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Cấu hình CORS (Cross-Origin Resource Sharing)
     * Cho phép các domain khác gọi tới API
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")    // Chỉ áp dụng CORS cho các API
                .allowedOrigins("*")                // Cho phép tất cả các domain (có thể sửa lại "http://localhost:8080)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void configureViewResolvers(org.springframework.web.servlet.config.annotation.ViewResolverRegistry registry) {
        registry.viewResolver(new org.thymeleaf.spring6.view.ThymeleafViewResolver());
    }
}
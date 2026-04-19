//package com.hms.meenakshi.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@RequiredArgsConstructor
//public class WebConfig implements WebMvcConfigurer {
//
//    private final SessionInterceptor sessionInterceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // Apply this only to manager and resident routes
//        registry.addInterceptor(sessionInterceptor)
//                .addPathPatterns("/manager/**", "/resident/**")
//                .excludePathPatterns("/auth/**", "/sign-in", "/css/**", "/js/**");
//    }
//}

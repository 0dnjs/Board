package com.korit.board.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // IOC 등록
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 요청 주소 (엔드포인트)
                .allowedMethods("*") // 모든 요청 메소드
                .allowedOrigins("*"); // 모든 요청 서버
        // 크로스오리진 풀어주겠다 (크로스오리진 설정)
    }
}

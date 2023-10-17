package com.korit.board.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity // 시큐리티로 쓰겠다
@Configuration // IOC 등록
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean // passwordEncoder 이름으로 IOC에 등록됨
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors(); // WebMvcConfig의 CORS 설정을 적용
        http.csrf().disable(); // 서브사이드렌더링 할때 쓰는것. 요청이 들어올때 csrf토큰이 필요한데 지금하는거에는 안씀
        http.authorizeRequests()
                .antMatchers("/auth/**")// 특정 url 지정 (auth로 시작하는 모든 url은)
                .permitAll(); // 위에 경로로 들어온 요청은 컨트롤러로 가는길을 막지 않겠다
    }
}

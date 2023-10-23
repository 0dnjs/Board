package com.korit.board.config;

import com.korit.board.Filter.JwtAuthenticationFilter;
import com.korit.board.security.PrincipalEntryPoint;
import com.korit.board.security.oauth2.OAuth2SuccessHandler;
import com.korit.board.service.PrincipalUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity // 시큐리티로 쓰겠다
@Configuration // IOC 등록
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalEntryPoint principalEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PrincipalUserDetailsService principalUserDetailsService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

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
                .permitAll() // 위에 경로로 들어온 요청은 컨트롤러로 가는길을 막지 않겠다
                .anyRequest() // 모든요청은
                .authenticated() // 인증거쳐라
                .and() // 그리고                                            // 여기서 예외터지면 principalentrypoint로 감
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 시큐리티컨텍스트홀더안에 어썬티케이션객체가 있냐없냐로 인증됐냐안됐냐체크함
                .exceptionHandling()
                .authenticationEntryPoint(principalEntryPoint)
                .and() // 이밑으로 oauth 설정부분
                .oauth2Login()
                .loginPage("http://localhost:3000/auth/signin")
                .successHandler(oAuth2SuccessHandler)
                .userInfoEndpoint()
                .userService(principalUserDetailsService);
    }
}

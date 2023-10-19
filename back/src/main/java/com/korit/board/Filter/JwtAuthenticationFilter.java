package com.korit.board.Filter;

import com.korit.board.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        // 요청들어왔을떄 헤더에서 Authorization를 꺼내라
        String bearerToken = httpServletRequest.getHeader("Authorization");

        String token = jwtProvider.getToken(bearerToken); // 토큰을 만들어서

        Authentication authentication = jwtProvider.getauthentication(token); // getauthentication jwt토큰을 푸는 작업을 함

        if(authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication); // 인증되고 DB에도 있으면 토큰 발행해줌
        }
        chain.doFilter(request, response); // 시큐리티 컨피그 유저네임패스워드 어센티케이션필터 클래스로감
    }
}

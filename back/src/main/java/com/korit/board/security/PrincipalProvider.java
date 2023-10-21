package com.korit.board.security;

import com.korit.board.service.PrincipalUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
// 사용자의 인증 정보를 검증하고 Authentication(사용자의 인증 정보를 나타냄) 객체를 생성하는 제공자
public class PrincipalProvider implements AuthenticationProvider {

    private final PrincipalUserDetailsService principalUserDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    //     사용자의 인증 정보를 나타냄
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String email = authentication.getName();
        String password = (String) authentication.getCredentials(); // password들어감 object로 들어가기 때문에 string으로 업캐스팅

        UserDetails PrincipalUser = principalUserDetailsService.loadUserByUsername(email); // 유저네임 못찾으면 여기서 예외터짐
        //                          클라이언트,     DB
        if (!passwordEncoder.matches(password, PrincipalUser.getPassword())) { // 암호화되지않은것과 암호화된것을 ture false 비교해줌
            throw new BadCredentialsException("BadCredentials"); // 비밀번호 틀리면 여기서 예외터짐
        }
                  // 위에 둘다 문제없이 일치하면 토큰을 발행해주는데 PrincipalUser안에 있는것들, 기본 패스워드,권한을 모두 가지고 인증
        return new UsernamePasswordAuthenticationToken(PrincipalUser, password, PrincipalUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

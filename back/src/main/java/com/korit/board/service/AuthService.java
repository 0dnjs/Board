package com.korit.board.service;

import com.korit.board.dto.SigninReqDto;
import com.korit.board.dto.SignupReqDto;
import com.korit.board.entity.User;
import com.korit.board.exception.DuplicateException;
import com.korit.board.jwt.JwtProvider;
import com.korit.board.repository.UserMapper;
import com.korit.board.security.PrincipalProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PrincipalProvider principalProvider;
    private final JwtProvider jwtProvider;


    public Boolean signup(SignupReqDto signupReqDto) { // 회원가입
        User user = signupReqDto.toUser(passwordEncoder);

        int errorCode = userMapper.checkDuplicate(user);
        if(errorCode > 0) {
            responseDuplicateError(errorCode);
        }

        return userMapper.saveUser(user) > 0;
    }

    private void responseDuplicateError(int errorCode) {
        Map<String,String> errorMap = new HashMap<>();
        switch (errorCode) {
            case 1:
                errorMap.put("email", "이미 사용중인 이메일입니다.");
                break ;
            case 2:
                errorMap.put("nickname", "이미 사용중인 닉네임입니다.");
                break;
            case 3:
                errorMap.put("email", "이미 사용중인 이메일입니다.");
                errorMap.put("nickname", "이미 사용중인 닉네임입니다.");
                break;
        }
        throw new DuplicateException(errorMap);

    }
                              // email,password 들어옴
    public String signin(SigninReqDto signinReqDto) {
        UsernamePasswordAuthenticationToken authenticationToken = // upcasting해서 받아옴
                new UsernamePasswordAuthenticationToken(signinReqDto.getEmail(), signinReqDto.getPassword());
        System.out.println(authenticationToken);
        // Object getCredentials();,public String getName();,public int hashCode();public String toString(); 등등 Principal을 상속받고있음
        Authentication authentication = principalProvider.authenticate(authenticationToken); // 여기서 email 미뤄진 예외터짐 유저네임낫파운드
        // principalProvider은 AuthenticationProvider를 상속받고있고 사용자 인증정보를 검증하고 토큰발행해주는 애임
        // 사실 principalProvider의 authenticate메소드가 검증하고 토큰발행해주는거임 authenticationToken은 기본 인증되지않은 데이터고
        // 그걸 principalProviderd에서 인증해서 authentication 여기 저장함


        // 인증된 토큰을 jwtProvider에서 시크릿키로 암호화한 토큰으로 생성해준다
        return jwtProvider.generateToken(authentication);
    }

    public boolean authenticate(String token) {
        // jwtProvider에서 토큰을 복호화해줌
        Claims claims = jwtProvider.getClaims(token);
        if(claims == null) {
            throw new JwtException("인증 토큰 유효성 검사 실패");
        }
        return Boolean.parseBoolean((claims.get("enabled").toString()));
    }
}

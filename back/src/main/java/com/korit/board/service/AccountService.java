package com.korit.board.service;

import com.korit.board.entity.User;
import com.korit.board.exception.AuthMailException;
import com.korit.board.jwt.JwtProvider;
import com.korit.board.repository.UserMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserMapper userMapper; // 매퍼가지고 인증을했다는 업데이트를 DB에 날려줌
    private final JwtProvider jwtProvider;

    @Transactional(rollbackFor = Exception.class)
    public boolean authenticateMail(String token) {
        Claims claims = jwtProvider.getClaims(token);
        if(claims == null) {         // 토큰 시간 만료됐거나 위조됐으면 예외처리됨
            throw new AuthMailException("만료된 인증 요청입니다.");
        }

        String email = claims.get("email").toString();
        System.out.println(email);
        User user = userMapper.findUserByEmail(email);
        if(user.getEnabled() > 0) {
            throw new AuthMailException("이미 인증이 완료된 요청입니다.");
        }

        return userMapper.updateEnabledToEmail(email) > 0;
    }
}

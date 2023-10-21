package com.korit.board.service;

import com.korit.board.dto.UpdatePasswordReqDto;
import com.korit.board.dto.UpdateProfileImgReqDto;
import com.korit.board.entity.User;
import com.korit.board.exception.AuthMailException;
import com.korit.board.exception.MismatchedPasswordException;
import com.korit.board.jwt.JwtProvider;
import com.korit.board.repository.UserMapper;
import com.korit.board.security.PrincipalUser;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserMapper userMapper; // 매퍼가지고 인증을했다는 업데이트를 DB에 날려줌
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;

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

    @Transactional(rollbackFor = Exception.class)
    public boolean updateProfileImg(UpdateProfileImgReqDto updateProfileImgReqDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userMapper.updateProfileUrl(User.builder()
                .email(email)
                .profileUrl(updateProfileImgReqDto.getProfileUrl())
                .build()) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(UpdatePasswordReqDto updatePasswordReqDto) {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalUser.getUser();

        if (!passwordEncoder.matches(updatePasswordReqDto.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("BadCredentials");
        }

        if(!Objects.equals(updatePasswordReqDto.getNewPassword(), updatePasswordReqDto.getCheckNewPassword())) {
            throw new MismatchedPasswordException();
        }

        user.setPassword(passwordEncoder.encode(updatePasswordReqDto.getNewPassword()));

        return userMapper.updatePassword(user) > 0;
    }
}

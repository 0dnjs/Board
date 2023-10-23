package com.korit.board.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.korit.board.aop.annotation.ArgsAop;
import com.korit.board.aop.annotation.ReturnAop;
import com.korit.board.aop.annotation.TimeAop;
import com.korit.board.aop.annotation.ValidAop;
import com.korit.board.dto.SigninReqDto;
import com.korit.board.dto.SignupReqDto;
import com.korit.board.exception.ValidException;
import com.korit.board.service.AccountService;
import com.korit.board.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AccountService accountService;

    @ArgsAop
    @ValidAop // 유효성 검사
    @PostMapping("/auth/signup")                             // email,password 요청하면 들어옴
    public ResponseEntity<?> signup( @RequestBody SignupReqDto signupReqDto) {

        return ResponseEntity.ok().body(authService.signup(signupReqDto));
    }

    @ReturnAop
    @ArgsAop
    @PostMapping("/auth/signin")
    public ResponseEntity<?> signin(@RequestBody SigninReqDto signinReqDto) {

        return ResponseEntity.ok(authService.signin(signinReqDto));
    }

    @GetMapping("/auth/token/authenticate")
    public ResponseEntity<?> authenticate(@RequestHeader(value = "Authorization") String token) {

        return ResponseEntity.ok(true);
    }

    @GetMapping("/auth/mail") //get요청보낼때 파라미터를 이 토큰으로 받아서 유효한지 확인
    public ResponseEntity<?> authenticateMail(String token) {
        return ResponseEntity.ok(accountService.authenticateMail(token) ? "인증이 완료되었습니다." : "인증 실패");
    }
}
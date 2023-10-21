package com.korit.board.controller;

import com.korit.board.aop.annotation.ArgsAop;
import com.korit.board.aop.annotation.ValidAop;
import com.korit.board.dto.PrincipalRespDto;
import com.korit.board.dto.UpdatePasswordReqDto;
import com.korit.board.dto.UpdateProfileImgReqDto;
import com.korit.board.entity.User;
import com.korit.board.security.PrincipalUser;
import com.korit.board.service.AccountService;
import com.korit.board.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final MailService mailService;
    private final AccountService accountService;

               // 프론트에서 요청 들어옴
    @GetMapping("/account/principal")
    public ResponseEntity<?> getPrincipal() {

        PrincipalUser principalUser =    // auth로시작하는 요청말고는 무조건 필터 걸쳐서 컨트롤러로 감
                (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // 프린시펄을 꺼냄 (다운캐스팅)
        User user = principalUser.getUser(); // db에서 꺼낸 유저가 있다
        PrincipalRespDto principalRespDto = user.toPrincipalDto();
        return ResponseEntity.ok(principalRespDto);
    }
                      //메일보냄
    @PostMapping("/account/mail/auth")
    public ResponseEntity<?> sendAuthenticationMail() {

        return ResponseEntity.ok(mailService.sendAuthMail()); // post요청들어오면 sendAuthMail이 날라감
    }

    @PutMapping("/account/profile/img")
    public ResponseEntity<?> updateProfileImg(@RequestBody UpdateProfileImgReqDto updateProfileImgReqDto) {
        return ResponseEntity.ok(accountService.updateProfileImg(updateProfileImgReqDto));
    }

    @ArgsAop
    @ValidAop
    @PutMapping("/account/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordReqDto updatePasswordReqDto, BindingResult bindingResult){

        return ResponseEntity.ok(accountService.updatePassword(updatePasswordReqDto));
    }
}

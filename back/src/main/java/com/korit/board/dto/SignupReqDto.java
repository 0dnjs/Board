package com.korit.board.dto;

import com.korit.board.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
public class SignupReqDto {
    @Email(message = "이메일 형식을 지켜주세요.")
    @NotBlank
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d~!@#$%^&*()+|=]*$", message = "숫자, 문자 중 무조건 한글자를 포함하세요.")
    @NotBlank
    private String password;

    @Pattern(regexp = "^[가-힣]*$", message = "한글만 입력 가능합니다.")
    @NotBlank
    private String name;
    @NotBlank
    private String nickname;

    private String oauth2Id;
    private String profileImg;
    private String provider;

    public User toUser(BCryptPasswordEncoder passwordEncoder) { // password 암호화 시켜줌
        return User.builder()
                .email(email)
                .password(passwordEncoder.encode(password)) // 암호화되어있어서 encode안에 password감싸줘야함
                .name(name)
                .nickname(nickname)
                .oauth2Id(oauth2Id)
                .provider(provider)
                .profileUrl(profileImg)
                .build();
    }
}

package com.korit.board.entity;


import com.korit.board.dto.PrincipalRespDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private int userId;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private int enabled; // DB에 1/0으로 저장함 활성화되고 안되고 기본값 0
    private String profileUrl;

    public PrincipalRespDto toPrincipalDto() {

        return PrincipalRespDto.builder()
                .userId(userId)
                .email(email)
                .name(name)
                .nickname(nickname)
                .enabled(enabled > 0)
                .profileUrl(profileUrl)
                .build();
    }
}

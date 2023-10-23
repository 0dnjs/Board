package com.korit.board.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PrincipalRespDto { // 응답으로 패스워드를 줄거라서 패스워드는 설정안함
    private int userId;
    private String email;
    private String name;
    private String nickname;
    private Boolean enabled;
    private String profileUrl;
    private String oauth2Id;
    private String provider;
}

package com.korit.board.security;

import com.korit.board.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
//          사용자의 정보와 권한을 저장
public class PrincipalUser implements UserDetails { // 사용자 정보를 담는 인터페이스, 로그인

    private User user;

    public PrincipalUser(User user) {
        this.user = user;
    }

    @Override                                        // 권한들
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { // 유효기간
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { // 블랙리스트
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { // 비밀번호 5번이상 틀리면
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled() > 0;
    } // 매니저빌더쓰면 얘가 먼저 실행되어서 authenticationProvier로한거고
    // 그걸하면 authenticate를 구현할수있음
}

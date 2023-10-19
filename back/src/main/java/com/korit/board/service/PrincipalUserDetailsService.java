package com.korit.board.service;

import com.korit.board.entity.User;
import com.korit.board.repository.UserMapper;
import com.korit.board.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalUserDetailsService implements UserDetailsService { // 유저를 불러와 UserDetails로 반환한다

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userMapper.findUserByEmail(email);

        if(user == null) {
            throw new UsernameNotFoundException("UsernameNotFound");
        }

        return new PrincipalUser(user);
    }
}

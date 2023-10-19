package com.korit.board.jwt;

import com.korit.board.entity.User;
import com.korit.board.repository.UserMapper;
import com.korit.board.security.PrincipalProvider;
import com.korit.board.security.PrincipalUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;
    private final UserMapper userMapper;

    public JwtProvider(@Value ("${jwt.secret}") String secret, @Autowired UserMapper userMapper) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.userMapper = userMapper;
    }

    public String generateToken(Authentication authentication) {

        String email = authentication.getName();

        Date date = new Date(new Date().getTime() + (1000 * 60 * 60 * 24)); // 24시간

        // 토큰 만들어줌
        return Jwts.builder()
                .setSubject("AccessToken")
                .setExpiration(date)
                .claim("email", email)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) {
// 토큰을 풀어헤지는 작업을하는데 인증이 지났거나 변조된 키면 여기서 예외 터짐 예외터지면 claims에 jwt가 대입이 안되기떄문에 null임
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch(Exception e) {
            System.out.println(e.getClass() + ": " + e.getMessage());
            //e.printStackTrace();
        }
        return claims; // 널이거나 클레임스객체를 반환해줌

    }

    public String getToken(String bearerToken) {
        if(!StringUtils.hasText(bearerToken)) {
            return null; // 토큰이 유효하지않다
        }
        return bearerToken.substring("Bearer ".length());
    }
// getauthentication  밑에 애들중에 토큰인증이 안됐거나 DB에 정보가 없으면 얘가 널을 리턴함
    public Authentication getauthentication(String token) {

        Claims claims = getClaims(token);

        if(claims == null) {
            return null; // 토큰은 유효하지만 유저객체가 없다
        }

        User user = userMapper.findUserByEmail(claims.get("email").toString());
        if (user == null) {
            return null;
        }

        PrincipalUser principalUser = new PrincipalUser(user);
        return new UsernamePasswordAuthenticationToken(principalUser, null, principalUser.getAuthorities());
    }

    public String generateAuthMailToken(String email) {
        Date date = new Date(new Date().getTime() + 1000 * 60 * 5); // 인증할수있는데까지 5분

        return Jwts.builder()
                .setSubject("AuthenticationEmailToken") // 토큰
                .setExpiration(date) // 만료시간
                .claim("email", email)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}

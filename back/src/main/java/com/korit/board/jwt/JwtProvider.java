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

    // 암호화한 키를 저장하는 객체
    private final Key key;
    private final UserMapper userMapper;
    // 인증된 토큰을 암호화된 jwt토큰으로 만들어줌
    public JwtProvider(@Value ("${jwt.secret}") String secret, @Autowired UserMapper userMapper) {
        // keys는 암호화해주는 객체
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.userMapper = userMapper;
    }

    //여기서 만든 메소드를 AuthService에 인증된 데이터를 받아서 암호화 처리를 해줌
    public String generateToken(Authentication authentication) {

        // Authentication이 Principal을 상속받는데 Principal안에 getName 메소드가 있음
        String email = authentication.getName();

        Date date = new Date(new Date().getTime() + (1000 * 60 * 60 * 24)); // 24시간

        // 토큰 만들어줌
        return Jwts.builder() // 암호화 해주는 코드
                .setSubject("AccessToken") // 토큰 제목,이름
                .setExpiration(date) // 인증유효 시간
                .claim("email", email) // 클레임 기반 권한부여는 클레임 값을 검사한 후에 이 값을 기반으로 리소스에 대한 접근을 허용함
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) {
// 토큰을 풀어헤치는 작업을하는데 인증이 지났거나 변조된 키면 여기서 예외 터짐 예외터지면 claims에 jwt가 대입이 안되기떄문에 null임
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder() // 복호화해주는 코드
                    .setSigningKey(key) // 시그니쳐 (비밀키를 포함하여 고유한 암호화 코드)
                    .build()
                    .parseClaimsJws(token) // 주어진 토큰을 파싱하여 JWS(JSON Web Signature) 객체로 변환
                    .getBody(); // JSW 객체에서 클레임을 추출하여 반환
        }catch(Exception e) {
            System.out.println(e.getClass() + ": " + e.getMessage());
            //e.printStackTrace();
        }
        return claims; // 널이거나 클레임스객체를 반환해줌

    }
        // bearerToken이 비어있거나 null인지 확인(토큰의 존재여부 확인하는 용도)
    public String getToken(String bearerToken) {
        if(!StringUtils.hasText(bearerToken)) {
            return null; // 토큰이 유효하지않다
        }
        return bearerToken.substring("Bearer ".length()); // bearer부분을 떼고 실제 JWT토큰 문자열을 반환함
    }
// getauthentication 밑에 애들중에 토큰인증이 안됐거나 DB에 정보가 없으면 얘가 널을 리턴함
    public Authentication getauthentication(String token) {

        Claims claims = getClaims(token);

        if(claims == null) {
            return null; // 토큰이 없으면 null로 반환
        }

        User user = userMapper.findUserByEmail(claims.get("email").toString());
        if (user == null) {
            return null; //토큰은 유효하지만 유저객체가 없다
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

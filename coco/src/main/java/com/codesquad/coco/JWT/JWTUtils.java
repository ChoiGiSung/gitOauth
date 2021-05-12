package com.codesquad.coco.JWT;

import io.jsonwebtoken.*;

import java.util.Date;

public class JWTUtils {
    private static final long TOKEN_VALID_IME = 6 * 60 * 60 * 1000L;

    public static String createToken(String id,String accessToken, String key){
        Claims claims = Jwts.claims();
        claims.put("id",id);
        claims.put("accessToken",accessToken);
        Date now = new Date();
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256,key)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TOKEN_VALID_IME))
                .compact();
    }

    public static void validateToken(String jwtToken, String key) throws OverTimeException {
        Jws<Claims> claimsJws;
        try{
            claimsJws = Jwts.parser().setSigningKey(key)
                    .parseClaimsJws(jwtToken);
        }catch (ExpiredJwtException e){
            //todo 시간 만료 예외 시 id로 DB에서 긁어와서 다시 요청
            // 예외 발생시 커스텀 예외로 던져주고 그걸 잡아서 처리한다 - > 이건 라이브러리를 사용하는 쪽에서 잡아라
            // 라이브러리는 예외를 던져 주기만 함
            throw new OverTimeException(jwtToken);
        }
    }

}

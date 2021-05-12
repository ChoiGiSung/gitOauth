package com.gitoauth.coco.JWT;

import io.jsonwebtoken.*;

import java.util.Date;

public class JWTUtils {
    private static final long TOKEN_VALID_IME = 6 * 60 * 60 * 1000L;
    private static final String TOKEN_TYPE = "Bearer";
    private static final String BLANK = " ";

    public static String createJWTTypeBearer(Long id, String key){

        Claims claims = Jwts.claims();
        claims.put("id",id);
        Date now = new Date();
        return TOKEN_TYPE+BLANK+Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, key)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + TOKEN_VALID_IME))
                .compact();
    }

    public static void validateJWT(String jwt, String key) throws OverTimeException {
        Jws<Claims> claimsJws;
        String[] tokens = jwt.split(BLANK);
        String tokenType =tokens[0];
        String realToken =tokens[1];
        try{
            claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(realToken);
        }catch (ExpiredJwtException e){
            //todo 시간 만료 예외 시 id로 DB에서 긁어와서 다시 요청
            // 예외 발생시 커스텀 예외로 던져주고 그걸 잡아서 처리한다 - > 이건 라이브러리를 사용하는 쪽에서 잡아라
            // 라이브러리는 예외를 던져 주기만 함
            throw new OverTimeException(jwt);
        }
    }

}

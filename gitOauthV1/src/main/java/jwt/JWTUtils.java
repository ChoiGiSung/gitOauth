package jwt;

import io.jsonwebtoken.*;
import oauth.UserInfoDTO;

import java.util.Date;

public class JWTUtils {

    public static final String HEADER_TYPE = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";
    private static final long TOKEN_VALID_TIME = 6 * 60 * 60 * 1000L;
    private static final String BLANK = " ";

    private JWTUtils() {
    }

    public static String createJWTTypeBearer(UserInfoDTO userInfoDTO, String key){
        Claims  claims = Jwts.claims();
        claims.put("id", userInfoDTO.getId());
        Date now = new Date();
        return TOKEN_TYPE+BLANK+Jwts.builder()
                .signWith(SignatureAlgorithm.HS256,key)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+TOKEN_VALID_TIME))
                .compact();
    }

    public static Jws<Claims> validateJWT(String jwt,String key){
        Jws<Claims> claimsJws;
        String[] tokens = jwt.split(BLANK);
        String tokenType = tokens[0];
        String jwtToken = tokens[1];

        try{
            claimsJws = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(jwtToken);
        }catch (ExpiredJwtException e){
            //todo 시간 만료 예외 시 id로 DB에서 긁어와서 다시 요청
            // 예외 발생시 커스텀 예외로 던져주고 그걸 잡아서 처리한다 - > 이건 라이브러리를 사용하는 쪽에서 잡아라
            // 라이브러리는 예외를 던져 주기만 함
            throw new OverTimeException();
        }

        return claimsJws;
    }

    public static Object getInfoFromJWT(String jwt,String key,String wantInfo){
        Jws<Claims> claimsJws = validateJWT(jwt,key);
        return claimsJws.getBody().get(wantInfo);
    }


}

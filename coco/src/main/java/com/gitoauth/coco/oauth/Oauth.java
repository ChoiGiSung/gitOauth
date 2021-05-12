package com.gitoauth.coco.oauth;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;

public class Oauth {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String accessTokenUri;
    private String authorizeUri;
    private String userInfoUri;
    private RestTemplate template = new RestTemplate();
    private String authorizeUrlFormat = "{0}?client_id={1}&redirect_uri={2}";


    public Oauth(String clientId, String clientSecret, String redirectUri, String accessTokenUri,
                 String authorizeUri, String userInfoUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.accessTokenUri = accessTokenUri;
        this.authorizeUri = authorizeUri;
        this.userInfoUri = userInfoUri;
    }

    public void requestCode(HttpServletResponse response) throws IOException {
        response.sendRedirect(MessageFormat.format(authorizeUrlFormat, authorizeUri,clientId, redirectUri));
    }

    public AccessToken requestAccessToken(String code){
        HttpEntity<RequestAccessTokenDTO> httpEntity = createHttpEntityForAccessToken(code);
        return template.exchange(accessTokenUri, HttpMethod.POST,httpEntity,AccessToken.class).getBody();
    }

    private HttpEntity<RequestAccessTokenDTO> createHttpEntityForAccessToken(String code){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(new RequestAccessTokenDTO(clientId,clientSecret,redirectUri,code),headers);
    }

    public UserInfoDTO requestUserInfo(AccessToken token){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(token.getAccessToken());
        HttpEntity<?> userInfo = new HttpEntity<>(headers);
        return template.exchange(userInfoUri,HttpMethod.GET,userInfo, UserInfoDTO.class).getBody();
    }


    public AccessToken requestRefreshToken(String refreshToken){
        HttpEntity<RequestRefreshTokenDTO> httpEntity = createHttpEntityForRefreshToken(refreshToken);
        return template.exchange(accessTokenUri, HttpMethod.POST,httpEntity,AccessToken.class).getBody();
    }

    private HttpEntity<RequestRefreshTokenDTO> createHttpEntityForRefreshToken(String refreshToken){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(new RequestRefreshTokenDTO(clientId,clientSecret,refreshToken),headers);
    }



    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private class RequestAccessTokenDTO {

        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String code;

        public RequestAccessTokenDTO() {
        }

        public RequestAccessTokenDTO(String clientId, String clientSecret, String redirectUri, String code) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.redirectUri = redirectUri;
            this.code = code;
        }

        public String getClientId() {
            return clientId;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public String getCode() {
            return code;
        }

        public String getClientSecret() {
            return clientSecret;
        }
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private class RequestRefreshTokenDTO {

        private String clientId;
        private String clientSecret;
        private final String grantType = "refresh_token";
        private String refreshToken;

        public RequestRefreshTokenDTO(String clientId, String clientSecret, String refreshToken) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.refreshToken = refreshToken;
        }

        public String getClientId() {
            return clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public String getGrantType() {
            return grantType;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}

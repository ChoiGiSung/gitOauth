package com.codesquad.coco.oauth;

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
    private String getUserInfoUri = "https://api.github.com/user";
    private RestTemplate template = new RestTemplate();
    private String authorizeUrlFormat = "{0}?client_id={1}&redirect_uri={2}";

    public Oauth(String clientId, String clientSecret, String redirectUri, String accessTokenUri, String authorizeUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.accessTokenUri = accessTokenUri;
        this.authorizeUri = authorizeUri;
    }

    public void requestCode(HttpServletResponse response) throws IOException {
        response.sendRedirect(MessageFormat.format(authorizeUrlFormat, authorizeUri,clientId, redirectUri));
    }

    public AccessToken requestAccessToken(String code){
        HttpEntity<RequestAccessTokenDTO> httpEntity = createHttpEntity(code);
        return template.exchange(accessTokenUri, HttpMethod.POST,httpEntity,AccessToken.class).getBody();
    }

    public UserInfoDTO requestUserInfo(AccessToken token){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(token.getAccessToken());
        HttpEntity<?> userInfo = new HttpEntity<>(headers);
        return template.exchange(getUserInfoUri,HttpMethod.GET,userInfo, UserInfoDTO.class).getBody();

    }

    private HttpEntity<RequestAccessTokenDTO> createHttpEntity(String code){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(new RequestAccessTokenDTO(clientId,clientSecret,redirectUri,code),headers);
    }
}

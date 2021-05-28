package oauth;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class Oauth {

    protected String clientId;
    protected String clientSecret;
    protected String redirectUri;
    protected String accessTokenUri;
    protected String userInfoUri;
    protected RestTemplate template = new RestTemplate();

    public Oauth() {
    }

    public Oauth(String clientId, String clientSecret, String redirectUri, String accessTokenUri, String userInfoUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.accessTokenUri = accessTokenUri;
        this.userInfoUri = userInfoUri;
    }

    public UserInfoDTO requestUserInfo(AccessToken token) {
        HttpHeaders headers = httpHeaderTypeJson();
        headers.setBearerAuth(token.getAccessToken());
        HttpEntity<?> userInfo = new HttpEntity<>(headers);
        return template.exchange(userInfoUri, HttpMethod.GET, userInfo, UserInfoDTO.class).getBody();
    }

    public AccessToken requestAccessToken(String code) {
        HttpHeaders headers = httpHeaderTypeJson();
        HttpEntity<RequestAccessTokenDTO> httpEntity =
                new HttpEntity<>(new RequestAccessTokenDTO(this.clientId, this.clientSecret, redirectUri, code), headers);
        return template.exchange(accessTokenUri, HttpMethod.POST, httpEntity, AccessToken.class).getBody();
    }

    public HttpHeaders httpHeaderTypeJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getAccessTokenUri() {
        return accessTokenUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public RestTemplate getTemplate() {
        return template;
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

        public String getClientSecret() {
            return clientSecret;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public String getCode() {
            return code;
        }

        @Override
        public String toString() {
            return "GitOauth{" +
                    "clientId='" + clientId + '\'' +
                    ", clientSecret='" + clientSecret + '\'' +
                    ", redirectUri='" + redirectUri + '\'' +
                    '}';
        }
    }

}

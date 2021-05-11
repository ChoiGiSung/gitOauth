package com.codesquad.coco.oauth;

public class RequestAccessTokenDTO {

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

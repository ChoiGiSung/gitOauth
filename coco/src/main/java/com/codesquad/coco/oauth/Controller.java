package com.codesquad.coco.oauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class Controller {//c0e41ec616dc9ae2c47fa44302d54aae057a0ff1

    private Oauth oauth = new Oauth("Iv1.d70653ab73d0f9fe","476ed3a436c49bb62d3ee98668ef7ebf06dc7b4b","http://localhost:8080/login/github",
            "https://github.com/login/oauth/access_token","https://github.com/login/oauth/authorize");

    @GetMapping("/login")
    public void login(HttpServletResponse response){
        try {
            oauth.requestCode(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/login/github")
    public void callBack(@RequestParam("code") String code){
        System.out.println(code);
        AccessToken accessToken = oauth.requestAccessToken(code);

        System.out.println(accessToken.toString());

        UserInfoDTO userInfoDTO = oauth.requestUserInfo(accessToken);
        System.out.println(userInfoDTO.toString());
    }
}

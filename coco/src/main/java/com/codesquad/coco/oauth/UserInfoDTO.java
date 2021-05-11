package com.codesquad.coco.oauth;

public class UserInfoDTO {

    private Long id;
    private String login;
    private String htmlUrl;
    private String location;
    private int followers;
    private int following;

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getLocation() {
        return location;
    }

    public int getFollowers() {
        return followers;
    }

    public int getFollowing() {
        return following;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", location='" + location + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                '}';
    }
}

package com.gitoauth.coco.JWT;

public class OverTimeException extends RuntimeException {

    private static String OVER_TIME_MESSAGE = "만료된 토큰입니다.:";

    public OverTimeException(String jwtToken) {
        super(OVER_TIME_MESSAGE+jwtToken);
    }
}

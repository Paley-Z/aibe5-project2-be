package com.ieum.ansimdonghaeng.domain.user.entity;

public enum AuthProvider {
    LOCAL("LOCAL"),
    KAKAO("KAKAO");

    private final String code;

    AuthProvider(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

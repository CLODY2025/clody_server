package com.clody.domain.member.entity;

public enum AccountScope {
    PUBLIC("공개"),
    FOLLOWERS_ONLY("팔로워만");

    private final String description;

    AccountScope(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
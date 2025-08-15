package com.clody.domain.hashtag.entity;

import lombok.Getter;

@Getter
public enum HashtagCategory {
    PADDED_COAT("패딩"),
    COAT("코트"),
    JACKET("자켓"),
    FLEECE("플리스"),
    KNIT("니트"),
    CARDIGAN("가디건"),
    HOODIE("후드"),
    WIND_BREAKER("바람막이"),
    SWEATSHIRT("맨투맨"),
    SHIRT("셔츠"),
    LONG_SLEEVE_TEE("긴팔티"),
    SHORT_SLEEVE_TEE("반팔티"),
    LONG_PANTS("롱팬츠"),
    SHORT_PANTS("숏팬츠"),
    SCARF("머플러"),
    BEANIE("비니"),
    SLEEVELESS("나시"),
    SANDALS("샌들"),
    SUNGLASSES("선글라스");

    private final String koreanName;

    HashtagCategory(String koreanName) {
        this.koreanName = koreanName;
    }
}

package com.clody.global.s3.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class S3UrlResponseDTO {
    //presigned url과 url 발급받은 key를 전달하는 dto
    private String preSignedUrl;
    private String key;
}
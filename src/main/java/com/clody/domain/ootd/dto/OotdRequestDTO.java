package com.clody.domain.ootd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

public class OotdRequestDTO {
    @Getter
    public static class CreateOotdDTO {
        @NotNull
        @Schema(description = "해당 날짜의 최저 기온", example = "24")
        private Integer minTemperature;

        @NotNull
        @Schema(description = "해당 날짜의 최고 기온", example = "33")
        private Integer maxTemperature;

        @NotNull
        @Schema(description = "해당 날짜의 습도", example = "30")
        private Integer humidity;

        @NotNull
        @Schema(description = "해당 날짜 비 여부", example = "true")
        @JsonProperty("rain")
        private Boolean rain;

        @NotBlank
        @Schema(description = "이미지 업로드 후 반환된 키", example = "ootd/12345/clody.png")
        private String key;

        @NotBlank
        @Schema(description = "해당 날짜의 날씨 한줄 설명", example = "맑음")
        private String weatherDescription;

        @NotEmpty
        @Schema(description = "입력된 해시태그 목록",  example = "[\"니트\",\"롱팬츠\"]")
        private List<String> hashtags;

    }
}

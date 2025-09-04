package com.clody.domain.ootd.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class OotdLikeResponseDTO {
    @Getter
    @AllArgsConstructor
    @Schema(description = "좋아요 상태 응답(내가 누른 상태인지 + 좋아요 개수)")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StateDTO {
        @Schema(description = "현재 내가 좋아요 눌렀는지", example = "true")
        private boolean liked;

        @Schema(description = "현재 좋아요 수", example = "12")
        private long likeCount;
    }

    @Getter
    @AllArgsConstructor
    @Schema(description = "좋아요 수만 응답")
    public static class CountDTO {
        @Schema(example = "12")
        private long likeCount;
    }
}

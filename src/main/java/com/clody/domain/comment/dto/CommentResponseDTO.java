package com.clody.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentResponseDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "식별자 응답(생성 결과)")
    public static class IdResponseDTO {
        @Schema(description = "리소스 ID", example = "456")
        private Long id;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "댓글 응답(트리 노드)")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CommentDTO {
        @Schema(example = "10")
        private Long id;

        @Schema(description = "작성자 닉네임", example = "클로디")
        private String nickname;

        @Schema(description = "작성 시각(서버 기준)", example = "2025-08-23T10:15:30")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdAt;

        @Schema(description = "댓글 내용", example = "이쁘네 ~~~ ")
        private String content;

        @Schema(description = "대댓글 목록(클로저 트리)")
        private List<CommentDTO> children = new ArrayList<>();
    }
}

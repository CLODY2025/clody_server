package com.clody.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


public class CommentRequestDTO {

    @Getter
    public static class CreateCommentDTO {
        @NotBlank
        @Schema(description = "댓글 내용", example = "안녀어엉")
        private String content;

        @Schema(description = "부모 댓글 ID(대댓글이면 넣기, 루트 댓글이면 null)", example = "1")
        private Long parentId;
    }

}

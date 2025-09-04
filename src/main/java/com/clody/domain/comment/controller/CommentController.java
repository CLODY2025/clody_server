package com.clody.domain.comment.controller;

import com.clody.domain.comment.dto.CommentRequestDTO;
import com.clody.domain.comment.dto.CommentResponseDTO;
import com.clody.domain.comment.service.commentCommandService.CommentCommandService;
import com.clody.domain.comment.service.commentQueryService.CommentQueryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ootd/{ootdId}/comments")
public class CommentController {

    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;

    @Operation(summary = "댓글 생성", description = "루트/대댓글 생성 API")
    @PostMapping
    public ResponseEntity<CommentResponseDTO.CreateResponseDTO> create(
            @PathVariable Long ootdId,
            @RequestParam Long memberId,
            @Valid @RequestBody CommentRequestDTO.CreateCommentDTO req
    ) {
        return ResponseEntity.ok(commentCommandService.add(ootdId, memberId, req));
    }

    @Operation(summary = "특정 ootd의 전체 댓글 조회", description = "특정 ootd의 전체 댓글 조회")
    @GetMapping("/all")
    public ResponseEntity<List<CommentResponseDTO.CommentDTO>> getAll(
            @PathVariable Long ootdId
    ) {
        return ResponseEntity.ok(commentQueryService.getAllComments(ootdId));
    }

    @Operation(summary = "댓글 삭제", description = "루트댓글이라면 하위댓글까지 전부 삭제")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long ootdId,
            @PathVariable Long commentId,
            @RequestParam Long memberId
    ) {
        commentCommandService.deleteCascade(commentId, memberId);
        return ResponseEntity.noContent().build();
    }
}

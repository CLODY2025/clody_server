package com.clody.domain.comment.service.commentCommandService;

import com.clody.domain.comment.dto.CommentRequestDTO;
import com.clody.domain.comment.dto.CommentResponseDTO;

public interface CommentCommandService {
    CommentResponseDTO.IdResponseDTO add(Long ootdId, Long memberId, CommentRequestDTO.CreateCommentDTO req);
    void deleteCascade(Long commentId, Long requesterId);
}

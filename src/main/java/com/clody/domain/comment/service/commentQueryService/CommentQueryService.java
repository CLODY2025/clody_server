package com.clody.domain.comment.service.commentQueryService;

import com.clody.domain.comment.dto.CommentResponseDTO;

import java.util.List;

public interface CommentQueryService {
    List<CommentResponseDTO.CommentDTO> getAllComments(Long ootdId);
    long countByOotd(Long ootdId);
}

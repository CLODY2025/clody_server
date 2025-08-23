package com.clody.domain.comment.service.commentQueryService;

import com.clody.domain.comment.dto.CommentResponseDTO;
import com.clody.domain.comment.entity.Comment;
import com.clody.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentQueryServiceImpl implements CommentQueryService {

    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    @Override
    public List<CommentResponseDTO.CommentDTO> getAllComments(Long ootdId) {
        List<Comment> all = commentRepository.findAllByOotdIdWithMember(ootdId);


        Map<Long, CommentResponseDTO.CommentDTO> nodeMap = new LinkedHashMap<>();
        for (Comment c : all) {
            nodeMap.put(c.getId(), new CommentResponseDTO.CommentDTO(
                    c.getId(),
                    c.getMember().getNickname(),
                    c.getCreatedAt(),
                    c.getContent(),
                    new ArrayList<>()
            ));
        }

        List<CommentResponseDTO.CommentDTO> roots = new ArrayList<>();
        for (Comment c : all) {
            CommentResponseDTO.CommentDTO dto = nodeMap.get(c.getId());
            if (c.getParent() == null) {
                roots.add(dto);
            } else {
                CommentResponseDTO.CommentDTO parentDto = nodeMap.get(c.getParent().getId());
                if (parentDto != null) parentDto.getChildren().add(dto);
                else roots.add(dto);
            }
        }
        return roots;
    }

    @Transactional(readOnly = true)
    @Override
    public long countByOotd(Long ootdId) {
        return commentRepository.countByOotdId(ootdId);
    }
}

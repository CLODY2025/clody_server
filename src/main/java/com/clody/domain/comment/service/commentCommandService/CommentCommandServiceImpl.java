package com.clody.domain.comment.service.commentCommandService;

import com.clody.domain.comment.dto.CommentRequestDTO;
import com.clody.domain.comment.dto.CommentResponseDTO;
import com.clody.domain.comment.entity.Comment;
import com.clody.domain.comment.exception.CommentErrorCode;
import com.clody.domain.comment.exception.CommentException;
import com.clody.domain.comment.repository.CommentClosureRepository;
import com.clody.domain.comment.repository.CommentRepository;
import com.clody.domain.member.entity.Member;
import com.clody.domain.member.repository.MemberRepository;
import com.clody.domain.ootd.entity.Ootd;
import com.clody.domain.ootd.exception.OotdErrorCode;
import com.clody.domain.ootd.exception.OotdException;
import com.clody.domain.ootd.repository.OotdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentCommandServiceImpl implements CommentCommandService {

    private final OotdRepository ootdRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final CommentClosureRepository closureRepository;

    @Transactional
    @Override
    public CommentResponseDTO.IdResponseDTO add(Long ootdId, Long memberId, CommentRequestDTO.CreateCommentDTO req) {
        Ootd ootd = ootdRepository.findById(ootdId)
                .orElseThrow(() -> new OotdException(OotdErrorCode.NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.MEMBER_NOT_FOUND));

        Comment parent = null;
        if (req.getParentId() != null) {
            parent = commentRepository.findById(req.getParentId())
                    .orElseThrow(() -> new CommentException(CommentErrorCode.PARENT_NOT_FOUND));
            if (!Objects.equals(parent.getOotd().getId(), ootdId)) {
                throw new CommentException(CommentErrorCode.PARENT_MISMATCH);
            }
        }

        Comment saved = commentRepository.save(Comment.builder()
                .ootd(ootd)
                .member(member)
                .content(req.getContent())
                .parent(parent)
                .build());

        //클로저 링크 추가
        if (parent == null) {
            closureRepository.insertSelfLink(saved.getId());              // (self,self,0)
        } else {
            closureRepository.insertAncestorLinksFromParent(parent.getId(), saved.getId()); // (A,new,depth+1)
            closureRepository.insertSelfLink(saved.getId());              // (new,new,0)
        }

        return new CommentResponseDTO.IdResponseDTO(saved.getId());
    }

    @Transactional
    @Override
    public void deleteCascade(Long commentId, Long requesterId) {
        Comment target = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.NOT_FOUND));
        if (!Objects.equals(target.getMember().getId(), requesterId)) {
            throw new CommentException(CommentErrorCode.FORBIDDEN_NOT_AUTHOR);
        }
        //모든 댓글 id 가져옴
        List<Long> ids = closureRepository.findSubtreeIds(commentId);
        if (ids == null || ids.isEmpty()) return;

        //클로저 링크 정리
        closureRepository.deleteLinksForIds(ids);

        commentRepository.deleteAllByIdInBatch(ids);
    }
}

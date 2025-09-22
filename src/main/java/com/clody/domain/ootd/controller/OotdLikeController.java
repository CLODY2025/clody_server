package com.clody.domain.ootd.controller;

import com.clody.domain.member.entity.Member;
import com.clody.domain.ootd.dto.OotdLikeResponseDTO;
import com.clody.domain.ootd.service.ootdLikeCommandService.OotdLikeCommandService;
import com.clody.domain.ootd.service.ootdLikeQueryService.OotdLikeQueryService;
import com.clody.global.auth.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ootd/{ootdId}/likes")
public class OotdLikeController {

    private final OotdLikeCommandService commandService;

    @Operation(summary = "좋아요 생성", description = "좋아요 생성")
    @PutMapping("/like")
    public ResponseEntity<?> like(@PathVariable Long ootdId,
                                  @CurrentUser Member memeber) {
        OotdLikeResponseDTO.StateDTO result = commandService.like(ootdId, memeber.getId());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "좋아요 취소", description = "좋아요 취소, 이미 취소 상태여도 가능")
    @DeleteMapping("/unlike")
    public ResponseEntity<?> unlike(@PathVariable Long ootdId,
                                    @CurrentUser Member memeber) {
        OotdLikeResponseDTO.StateDTO result = commandService.unlike(ootdId, memeber.getId());
        return ResponseEntity.ok(result);
    }

}
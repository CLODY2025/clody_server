package com.clody.domain.ootd.controller;

import com.clody.domain.member.entity.Member;
import com.clody.domain.ootd.dto.OotdRequestDTO;
import com.clody.domain.ootd.dto.OotdResponseDTO;
import com.clody.domain.ootd.service.ootdCommandService.OotdCommandService;
import com.clody.domain.ootd.service.ootdQueryService.OotdQueryService;
import com.clody.global.apiPayload.ApiResponse;
import com.clody.global.auth.CurrentUser;
import com.clody.global.s3.dto.S3UrlResponseDTO;
import com.clody.global.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/ootd")
@Tag(name = "ootd 관련 API", description = "ootd 관련 API입니다.")
public class OotdController {
    private final S3Service s3Service;
    private final OotdQueryService ootdQueryService;
    private final OotdCommandService ootdCommandService;

    @GetMapping("/upload-url")
    @Operation(summary = "Ootd 이미지 업로드 Presigned URL 요청 API", description = "ootd 생성 전에 이미지 먼저 업로드 하신 뒤, 반환된 key를 ootd 생성시에 넘겨주시면 됩니다.")
    public ResponseEntity<ApiResponse<S3UrlResponseDTO>> getPutPresignedUrl(
            @RequestParam String fileName){
        S3UrlResponseDTO result = s3Service.getPutGeneratePresignedUrlRequest(fileName,"ootd");
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    @PostMapping("/create")
    @Operation(summary = "ootd 생성 API", description = "ootd를 생성하는 API입니다.")
    public ResponseEntity<ApiResponse<OotdResponseDTO.getOotdDTO>> createOotd(
            @Valid @RequestBody OotdRequestDTO.CreateOotdDTO request, @CurrentUser Member member){
        OotdResponseDTO.getOotdDTO result=ootdCommandService.createOotd(request, member);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    @GetMapping("/community")
    @Operation(summary = "ootd 둘러보기 API", description = "ootd 둘러보기 API입니다.")
    public ResponseEntity<ApiResponse<OotdResponseDTO.getCommunityOotdListDTO>> getCommunity(
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, name = "hashtags") List<String> hashtagsKorean
    ) {
        OotdResponseDTO.getCommunityOotdListDTO result = ootdQueryService.getCommunityOotds(cursor, size, hashtagsKorean);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    @GetMapping("/community/{ootdId}")
    @Operation(summary = "ootd 둘러보기 상세 조회 API", description = "ootd 둘러보기에서 상세 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<OotdResponseDTO.getOotdDTO>> getCommunityOotdDetail(@PathVariable Long ootdId){
        OotdResponseDTO.getOotdDTO result=ootdQueryService.getOotd(ootdId);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    @GetMapping("/month")
    @Operation(summary = "개인 월별 ootd 조회 API", description = "개인 월별 ootd 리스트를 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<OotdResponseDTO.getMonthlyOotdListDTO>> getMonthlyOotd(@RequestParam int year,
                                                                                             @RequestParam int month,
                                                                                             @CurrentUser Member member){
        OotdResponseDTO.getMonthlyOotdListDTO result=ootdQueryService.getMonthlyOotds(year,month,member.getId());
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }
    @GetMapping("/month/{ootdId}")
    @Operation(summary = "월별 ootd 상세 조회 API", description = "월별 ootd 상세 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<OotdResponseDTO.getOotdDTO>> getMonthlyOotdDetail(@PathVariable Long ootdId){
        OotdResponseDTO.getOotdDTO result=ootdQueryService.getOotd(ootdId);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    @GetMapping("/similar-ootd")
    @Operation(summary = "비슷한 기온 ootd 조회 API", description = "비슷한 기온 ootd 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<OotdResponseDTO.getSimilarOotdListDTO>> getSimilarOotd(@RequestParam int minTemp,
                                                                                             @RequestParam int maxTemp,
                                                                                             @RequestParam boolean rain,
                                                                                             @CurrentUser Member member){
        OotdResponseDTO.getSimilarOotdListDTO result=ootdQueryService.getRandomSimilarOotds(minTemp, maxTemp, rain, member.getId());
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    @GetMapping("/similar-ootd/{ootdId}")
    @Operation(summary = "비슷한 기온 ootd 상세 조회 API", description = "비슷한 기온 ootd 상세 조회하는 API입니다.")
    public ResponseEntity<ApiResponse<OotdResponseDTO.getOotdDTO>> getSimilarOotdDetail(@PathVariable Long ootdId){
        OotdResponseDTO.getOotdDTO result=ootdQueryService.getOotd(ootdId);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

//    // 팔로워용 API
//    @GetMapping("/{nickName}")
//    @Operation(summary = "친구 월별 ootd 조회 API", description = "친구 월별 ootd 조회 API하는 API입니다.")
//    public ResponseEntity<ApiResponse<OotdResponseDTO.getMonthlyOotdListDTO>> getFollowOotd(@PathVariable String nickName, Member member,@RequestParam int year,
//                                                                                  @RequestParam int month){
//        OotdResponseDTO.getMonthlyOotdListDTO result=ootdQueryService.getMonthlyOotds(year,month);
//        return ResponseEntity.ok(ApiResponse.onSuccess(result));
//    }
//    @GetMapping("/{nickName}/{ootdId}")
//    @Operation(summary = "월별 ootd 상세 조회 API", description = "월별 ootd 상세 조회하는 API입니다.")
//    public ApiResponse<OotdResponseDTO.getOotdDTO> getFollowOotdDetail(@PathVariable String nickName, @PathVariable Long ootdId, Member member){
//        OotdResponseDTO.getOotdDTO result=ootdQueryService.getOotd(ootdId);
//        return ResponseEntity.ok(ApiResponse.onSuccess(result));
//    }

}

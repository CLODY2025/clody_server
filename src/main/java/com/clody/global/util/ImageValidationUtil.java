package com.clody.global.util;

import com.clody.domain.member.exception.MemberErrorCode;
import com.clody.domain.member.exception.MemberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class ImageValidationUtil {
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("빈 파일이 업로드 되었습니다");
            throw new MemberException(MemberErrorCode.INVALID_IMAGE_FILE);
        }

        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("파일 크기가 너무 큽니다. 업로드된 크기: {}bytes, 최대 허용: {}bytes", 
                    file.getSize(), MAX_FILE_SIZE);
            throw new MemberException(MemberErrorCode.IMAGE_FILE_TOO_LARGE);
        }

        // Content-Type 검증
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            log.warn("허용되지 않는 파일 타입입니다. Content-Type: {}", contentType);
            throw new MemberException(MemberErrorCode.INVALID_IMAGE_FORMAT);
        }

        // 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            log.warn("파일명이 없습니다");
            throw new MemberException(MemberErrorCode.INVALID_IMAGE_FILE);
        }

        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            log.warn("허용되지 않는 파일 확장자입니다. 확장자: {}", extension);
            throw new MemberException(MemberErrorCode.INVALID_IMAGE_FORMAT);
        }

        log.info("이미지 파일 검증 완료 - 파일명: {}, 크기: {}bytes, Content-Type: {}", 
                originalFilename, file.getSize(), contentType);
    }

    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    public static String generateProfileImageKey(Long memberId, String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return String.format("profile/%d/%d_%s.%s", 
                memberId, 
                System.currentTimeMillis(), 
                java.util.UUID.randomUUID().toString().substring(0, 8),
                extension);
    }
}
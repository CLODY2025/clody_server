package com.clody.domain.ootd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class OotdResponseDTO {
    @Getter
    @Builder
    public static class getOotdDTO {
        private Long id;
        private String nickname;
        private String image;
        private List<String> hashtags;
        private Long commentCount;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class getCommunityOotdListDTO {
        private List<getOotdDTO> items;
        private Long nextCursor;
        private boolean hasNext;
    }

    @Getter
    @Builder
    public static class getMonthlyOotdDTO {
        private Long id;
        private LocalDateTime date;
    }

    @Getter
    @Builder
    public static class getMonthlyOotdListDTO {
        private List<getMonthlyOotdDTO> ootds;
    }

    @Getter
    @Builder
    public static class getSimilarOotdDTO {
        private Long id;
        private String image;
        private List<String> hashtags;
    }

    @Getter
    @Builder
    public static class getSimilarOotdListDTO {
        private List<getSimilarOotdDTO> ootds;
    }

}

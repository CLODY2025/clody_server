package com.clody.domain.ootdHashtag.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class OotdHashtagResponseDTO {
    @Getter
    @AllArgsConstructor
    public class OotdHashtagCategoryDTO {
        private Long ootdId;
        private String category;
    }
}

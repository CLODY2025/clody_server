package com.clody.domain.ootd.service.ootdCommandService;

import com.clody.domain.ootd.dto.OotdRequestDTO;
import com.clody.domain.ootd.dto.OotdResponseDTO;

public interface OotdCommandService {
    OotdResponseDTO.getOotdDTO createOotd(OotdRequestDTO.CreateOotdDTO request);
}

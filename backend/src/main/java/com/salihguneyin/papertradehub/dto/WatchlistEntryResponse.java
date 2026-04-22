package com.salihguneyin.papertradehub.dto;

import com.salihguneyin.papertradehub.entity.WatchlistStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record WatchlistEntryResponse(
        Long id,
        Long assetId,
        String symbol,
        String assetName,
        WatchlistStatus status,
        BigDecimal targetPrice,
        Integer convictionScore,
        String setupNotes,
        LocalDate createdAt
) {
}

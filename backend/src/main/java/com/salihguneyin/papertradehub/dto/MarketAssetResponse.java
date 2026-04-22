package com.salihguneyin.papertradehub.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MarketAssetResponse(
        Long id,
        String symbol,
        String name,
        String assetClass,
        BigDecimal currentPrice,
        BigDecimal dailyChangePercent,
        String thesis,
        boolean active,
        LocalDate createdAt
) {
}

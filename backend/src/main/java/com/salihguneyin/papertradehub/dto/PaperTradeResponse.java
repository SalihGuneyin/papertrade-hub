package com.salihguneyin.papertradehub.dto;

import com.salihguneyin.papertradehub.entity.TradeSide;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaperTradeResponse(
        Long id,
        Long assetId,
        String symbol,
        String assetName,
        TradeSide side,
        BigDecimal quantity,
        BigDecimal executionPrice,
        String strategyTag,
        String tradeNotes,
        LocalDate executedAt,
        LocalDateTime updatedAt
) {
}

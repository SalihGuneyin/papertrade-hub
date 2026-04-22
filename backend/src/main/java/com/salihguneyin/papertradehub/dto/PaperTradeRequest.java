package com.salihguneyin.papertradehub.dto;

import com.salihguneyin.papertradehub.entity.TradeSide;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PaperTradeRequest(
        @NotNull Long assetId,
        @NotNull TradeSide side,
        @NotNull @DecimalMin("0.000001") BigDecimal quantity,
        @NotNull @DecimalMin("0.01") BigDecimal executionPrice,
        @NotBlank String strategyTag,
        @NotBlank String tradeNotes
) {
}

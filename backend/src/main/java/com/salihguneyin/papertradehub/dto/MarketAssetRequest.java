package com.salihguneyin.papertradehub.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record MarketAssetRequest(
        @NotBlank String symbol,
        @NotBlank String name,
        @NotBlank String assetClass,
        @NotNull @DecimalMin("0.01") BigDecimal currentPrice,
        @NotNull BigDecimal dailyChangePercent,
        @NotBlank String thesis,
        boolean active
) {
}

package com.salihguneyin.papertradehub.dto;

import com.salihguneyin.papertradehub.entity.WatchlistStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record WatchlistEntryRequest(
        @NotNull Long assetId,
        @NotNull WatchlistStatus status,
        @NotNull @DecimalMin("0.01") BigDecimal targetPrice,
        @NotNull @Min(1) @Max(100) Integer convictionScore,
        @NotBlank String setupNotes
) {
}

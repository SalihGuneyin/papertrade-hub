package com.salihguneyin.papertradehub.dto;

import java.util.List;

public record DashboardResponse(
        List<SummaryCardResponse> summary,
        List<PipelineMetricResponse> pipeline,
        List<PaperTradeResponse> recentTrades
) {
}

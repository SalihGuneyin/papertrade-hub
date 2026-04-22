package com.salihguneyin.papertradehub.service;

import com.salihguneyin.papertradehub.dto.DashboardResponse;
import com.salihguneyin.papertradehub.dto.PipelineMetricResponse;
import com.salihguneyin.papertradehub.dto.SummaryCardResponse;
import com.salihguneyin.papertradehub.entity.WatchlistStatus;
import com.salihguneyin.papertradehub.repository.MarketAssetRepository;
import com.salihguneyin.papertradehub.repository.PaperTradeRepository;
import com.salihguneyin.papertradehub.repository.WatchlistEntryRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final MarketAssetRepository marketAssetRepository;
    private final WatchlistEntryRepository watchlistEntryRepository;
    private final PaperTradeRepository paperTradeRepository;
    private final TradeService tradeService;

    public DashboardService(
            MarketAssetRepository marketAssetRepository,
            WatchlistEntryRepository watchlistEntryRepository,
            PaperTradeRepository paperTradeRepository,
            TradeService tradeService
    ) {
        this.marketAssetRepository = marketAssetRepository;
        this.watchlistEntryRepository = watchlistEntryRepository;
        this.paperTradeRepository = paperTradeRepository;
        this.tradeService = tradeService;
    }

    public DashboardResponse getDashboard() {
        List<SummaryCardResponse> summary = List.of(
                new SummaryCardResponse("Tracked Assets", marketAssetRepository.count(), "ink"),
                new SummaryCardResponse("Watchlist Entries", watchlistEntryRepository.count(), "mint"),
                new SummaryCardResponse("Ready Setups", watchlistEntryRepository.countByStatus(WatchlistStatus.READY), "gold"),
                new SummaryCardResponse("Trades Logged", paperTradeRepository.count(), "rose")
        );

        List<PipelineMetricResponse> pipeline = Arrays.stream(WatchlistStatus.values())
                .map(status -> new PipelineMetricResponse(status.name(), watchlistEntryRepository.countByStatus(status)))
                .toList();

        return new DashboardResponse(summary, pipeline, tradeService.getRecent());
    }
}

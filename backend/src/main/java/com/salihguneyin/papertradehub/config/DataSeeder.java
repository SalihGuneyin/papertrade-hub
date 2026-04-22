package com.salihguneyin.papertradehub.config;

import com.salihguneyin.papertradehub.entity.MarketAsset;
import com.salihguneyin.papertradehub.entity.PaperTrade;
import com.salihguneyin.papertradehub.entity.TradeSide;
import com.salihguneyin.papertradehub.entity.WatchlistEntry;
import com.salihguneyin.papertradehub.entity.WatchlistStatus;
import com.salihguneyin.papertradehub.repository.MarketAssetRepository;
import com.salihguneyin.papertradehub.repository.PaperTradeRepository;
import com.salihguneyin.papertradehub.repository.WatchlistEntryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            MarketAssetRepository marketAssetRepository,
            WatchlistEntryRepository watchlistEntryRepository,
            PaperTradeRepository paperTradeRepository
    ) {
        return args -> {
            if (marketAssetRepository.count() > 0) {
                return;
            }

            MarketAsset btc = asset("BTC", "Bitcoin", "Crypto", "68450.00", "2.85", "Watching for higher low above prior breakout level.", true);
            MarketAsset eth = asset("ETH", "Ethereum", "Crypto", "3520.00", "1.48", "Monitoring ETF-driven momentum continuation.", true);
            MarketAsset sol = asset("SOL", "Solana", "Crypto", "182.40", "-1.92", "Looking for reclaim after pullback into daily support.", true);
            MarketAsset link = asset("LINK", "Chainlink", "Crypto", "18.75", "0.66", "Waiting for range expansion with volume confirmation.", false);
            marketAssetRepository.save(btc);
            marketAssetRepository.save(eth);
            marketAssetRepository.save(sol);
            marketAssetRepository.save(link);

            watchlistEntryRepository.save(entry(btc, WatchlistStatus.READY, "70000.00", 83, "Breakout retest looks constructive."));
            watchlistEntryRepository.save(entry(eth, WatchlistStatus.ACTIVE, "3650.00", 76, "Need stronger relative strength first."));
            watchlistEntryRepository.save(entry(sol, WatchlistStatus.ACTIVE, "195.00", 72, "Still valid if higher timeframe support holds."));
            watchlistEntryRepository.save(entry(link, WatchlistStatus.PAUSED, "20.50", 54, "Paused until market breadth improves."));

            paperTradeRepository.save(trade(btc, TradeSide.BUY, "0.150000", "65200.00", "Trend continuation", "Took starter position after reclaim of daily VWAP.", LocalDate.now().minusDays(8)));
            paperTradeRepository.save(trade(sol, TradeSide.BUY, "8.000000", "176.00", "Support bounce", "Risk defined below local structure low.", LocalDate.now().minusDays(5)));
            paperTradeRepository.save(trade(eth, TradeSide.SELL, "1.200000", "3485.00", "Range trim", "Reduced exposure near overhead resistance.", LocalDate.now().minusDays(2)));
        };
    }

    private MarketAsset asset(
            String symbol,
            String name,
            String assetClass,
            String currentPrice,
            String dailyChangePercent,
            String thesis,
            boolean active
    ) {
        MarketAsset asset = new MarketAsset();
        asset.setSymbol(symbol);
        asset.setName(name);
        asset.setAssetClass(assetClass);
        asset.setCurrentPrice(new BigDecimal(currentPrice));
        asset.setDailyChangePercent(new BigDecimal(dailyChangePercent));
        asset.setThesis(thesis);
        asset.setActive(active);
        return asset;
    }

    private WatchlistEntry entry(
            MarketAsset asset,
            WatchlistStatus status,
            String targetPrice,
            int convictionScore,
            String setupNotes
    ) {
        WatchlistEntry entry = new WatchlistEntry();
        entry.setAsset(asset);
        entry.setStatus(status);
        entry.setTargetPrice(new BigDecimal(targetPrice));
        entry.setConvictionScore(convictionScore);
        entry.setSetupNotes(setupNotes);
        return entry;
    }

    private PaperTrade trade(
            MarketAsset asset,
            TradeSide side,
            String quantity,
            String executionPrice,
            String strategyTag,
            String tradeNotes,
            LocalDate executedAt
    ) {
        PaperTrade trade = new PaperTrade();
        trade.setAsset(asset);
        trade.setSide(side);
        trade.setQuantity(new BigDecimal(quantity));
        trade.setExecutionPrice(new BigDecimal(executionPrice));
        trade.setStrategyTag(strategyTag);
        trade.setTradeNotes(tradeNotes);
        trade.setExecutedAt(executedAt);
        return trade;
    }
}

package com.salihguneyin.papertradehub.service;

import com.salihguneyin.papertradehub.dto.WatchlistEntryRequest;
import com.salihguneyin.papertradehub.dto.WatchlistEntryResponse;
import com.salihguneyin.papertradehub.entity.WatchlistEntry;
import com.salihguneyin.papertradehub.repository.WatchlistEntryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WatchlistService {

    private final WatchlistEntryRepository watchlistEntryRepository;
    private final MarketAssetService marketAssetService;

    public WatchlistService(
            WatchlistEntryRepository watchlistEntryRepository,
            MarketAssetService marketAssetService
    ) {
        this.watchlistEntryRepository = watchlistEntryRepository;
        this.marketAssetService = marketAssetService;
    }

    public List<WatchlistEntryResponse> getAll() {
        return watchlistEntryRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public WatchlistEntryResponse create(WatchlistEntryRequest request) {
        WatchlistEntry entry = new WatchlistEntry();
        entry.setAsset(marketAssetService.getEntity(request.assetId()));
        entry.setStatus(request.status());
        entry.setTargetPrice(request.targetPrice());
        entry.setConvictionScore(request.convictionScore());
        entry.setSetupNotes(request.setupNotes().trim());

        return toResponse(watchlistEntryRepository.save(entry));
    }

    private WatchlistEntryResponse toResponse(WatchlistEntry entry) {
        return new WatchlistEntryResponse(
                entry.getId(),
                entry.getAsset().getId(),
                entry.getAsset().getSymbol(),
                entry.getAsset().getName(),
                entry.getStatus(),
                entry.getTargetPrice(),
                entry.getConvictionScore(),
                entry.getSetupNotes(),
                entry.getCreatedAt()
        );
    }
}

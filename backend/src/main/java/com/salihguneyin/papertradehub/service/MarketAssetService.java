package com.salihguneyin.papertradehub.service;

import com.salihguneyin.papertradehub.dto.MarketAssetRequest;
import com.salihguneyin.papertradehub.dto.MarketAssetResponse;
import com.salihguneyin.papertradehub.entity.MarketAsset;
import com.salihguneyin.papertradehub.exception.NotFoundException;
import com.salihguneyin.papertradehub.repository.MarketAssetRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MarketAssetService {

    private final MarketAssetRepository marketAssetRepository;

    public MarketAssetService(MarketAssetRepository marketAssetRepository) {
        this.marketAssetRepository = marketAssetRepository;
    }

    public List<MarketAssetResponse> getAll() {
        return marketAssetRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public MarketAsset getEntity(Long id) {
        return marketAssetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Asset not found"));
    }

    public MarketAssetResponse create(MarketAssetRequest request) {
        if (marketAssetRepository.existsBySymbolIgnoreCase(request.symbol())) {
            throw new IllegalArgumentException("An asset with this symbol already exists");
        }

        MarketAsset asset = new MarketAsset();
        asset.setSymbol(request.symbol().trim().toUpperCase());
        asset.setName(request.name().trim());
        asset.setAssetClass(request.assetClass().trim());
        asset.setCurrentPrice(request.currentPrice());
        asset.setDailyChangePercent(request.dailyChangePercent());
        asset.setThesis(request.thesis().trim());
        asset.setActive(request.active());

        return toResponse(marketAssetRepository.save(asset));
    }

    public MarketAssetResponse toResponse(MarketAsset asset) {
        return new MarketAssetResponse(
                asset.getId(),
                asset.getSymbol(),
                asset.getName(),
                asset.getAssetClass(),
                asset.getCurrentPrice(),
                asset.getDailyChangePercent(),
                asset.getThesis(),
                asset.isActive(),
                asset.getCreatedAt()
        );
    }
}

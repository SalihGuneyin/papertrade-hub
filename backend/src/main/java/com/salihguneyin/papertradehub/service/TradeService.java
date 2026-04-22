package com.salihguneyin.papertradehub.service;

import com.salihguneyin.papertradehub.dto.PaperTradeRequest;
import com.salihguneyin.papertradehub.dto.PaperTradeResponse;
import com.salihguneyin.papertradehub.entity.PaperTrade;
import com.salihguneyin.papertradehub.repository.PaperTradeRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TradeService {

    private final PaperTradeRepository paperTradeRepository;
    private final MarketAssetService marketAssetService;

    public TradeService(PaperTradeRepository paperTradeRepository, MarketAssetService marketAssetService) {
        this.paperTradeRepository = paperTradeRepository;
        this.marketAssetService = marketAssetService;
    }

    public List<PaperTradeResponse> getAll() {
        return paperTradeRepository.findAllByOrderByUpdatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PaperTradeResponse> getRecent() {
        return paperTradeRepository.findTop6ByOrderByUpdatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public PaperTradeResponse create(PaperTradeRequest request) {
        PaperTrade trade = new PaperTrade();
        trade.setAsset(marketAssetService.getEntity(request.assetId()));
        trade.setSide(request.side());
        trade.setQuantity(request.quantity());
        trade.setExecutionPrice(request.executionPrice());
        trade.setStrategyTag(request.strategyTag().trim());
        trade.setTradeNotes(request.tradeNotes().trim());

        return toResponse(paperTradeRepository.save(trade));
    }

    private PaperTradeResponse toResponse(PaperTrade trade) {
        return new PaperTradeResponse(
                trade.getId(),
                trade.getAsset().getId(),
                trade.getAsset().getSymbol(),
                trade.getAsset().getName(),
                trade.getSide(),
                trade.getQuantity(),
                trade.getExecutionPrice(),
                trade.getStrategyTag(),
                trade.getTradeNotes(),
                trade.getExecutedAt(),
                trade.getUpdatedAt()
        );
    }
}

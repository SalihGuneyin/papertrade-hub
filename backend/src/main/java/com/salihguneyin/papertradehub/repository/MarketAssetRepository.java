package com.salihguneyin.papertradehub.repository;

import com.salihguneyin.papertradehub.entity.MarketAsset;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketAssetRepository extends JpaRepository<MarketAsset, Long> {
    boolean existsBySymbolIgnoreCase(String symbol);
    List<MarketAsset> findAllByOrderByCreatedAtDesc();
}

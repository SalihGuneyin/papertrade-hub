package com.salihguneyin.papertradehub.repository;

import com.salihguneyin.papertradehub.entity.PaperTrade;
import com.salihguneyin.papertradehub.entity.TradeSide;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperTradeRepository extends JpaRepository<PaperTrade, Long> {
    long countBySide(TradeSide side);
    List<PaperTrade> findTop6ByOrderByUpdatedAtDesc();
    List<PaperTrade> findAllByOrderByUpdatedAtDesc();
}

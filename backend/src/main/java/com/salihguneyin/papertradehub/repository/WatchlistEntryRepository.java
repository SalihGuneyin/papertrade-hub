package com.salihguneyin.papertradehub.repository;

import com.salihguneyin.papertradehub.entity.WatchlistEntry;
import com.salihguneyin.papertradehub.entity.WatchlistStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistEntryRepository extends JpaRepository<WatchlistEntry, Long> {
    long countByStatus(WatchlistStatus status);
    List<WatchlistEntry> findAllByOrderByCreatedAtDesc();
}

package com.salihguneyin.papertradehub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "watchlist_entries")
public class WatchlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private MarketAsset asset;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WatchlistStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal targetPrice;

    @Column(nullable = false)
    private Integer convictionScore;

    @Column(nullable = false, length = 1000)
    private String setupNotes;

    @Column(nullable = false)
    private LocalDate createdAt;

    @PrePersist
    void prePersist() {
        createdAt = createdAt == null ? LocalDate.now() : createdAt;
    }
}

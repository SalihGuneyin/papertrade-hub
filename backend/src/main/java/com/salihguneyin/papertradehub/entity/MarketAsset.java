package com.salihguneyin.papertradehub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "market_assets")
public class MarketAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String symbol;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String assetClass;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal currentPrice;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal dailyChangePercent;

    @Column(nullable = false, length = 500)
    private String thesis;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDate createdAt;

    @PrePersist
    void prePersist() {
        createdAt = createdAt == null ? LocalDate.now() : createdAt;
    }
}

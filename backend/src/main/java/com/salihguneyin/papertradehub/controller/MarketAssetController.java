package com.salihguneyin.papertradehub.controller;

import com.salihguneyin.papertradehub.dto.MarketAssetRequest;
import com.salihguneyin.papertradehub.dto.MarketAssetResponse;
import com.salihguneyin.papertradehub.service.MarketAssetService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assets")
public class MarketAssetController {

    private final MarketAssetService marketAssetService;

    public MarketAssetController(MarketAssetService marketAssetService) {
        this.marketAssetService = marketAssetService;
    }

    @GetMapping
    public List<MarketAssetResponse> getAll() {
        return marketAssetService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarketAssetResponse create(@Valid @RequestBody MarketAssetRequest request) {
        return marketAssetService.create(request);
    }
}

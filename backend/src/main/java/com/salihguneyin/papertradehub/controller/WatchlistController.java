package com.salihguneyin.papertradehub.controller;

import com.salihguneyin.papertradehub.dto.WatchlistEntryRequest;
import com.salihguneyin.papertradehub.dto.WatchlistEntryResponse;
import com.salihguneyin.papertradehub.service.WatchlistService;
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
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public List<WatchlistEntryResponse> getAll() {
        return watchlistService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WatchlistEntryResponse create(@Valid @RequestBody WatchlistEntryRequest request) {
        return watchlistService.create(request);
    }
}

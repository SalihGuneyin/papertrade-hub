package com.salihguneyin.papertradehub.controller;

import com.salihguneyin.papertradehub.dto.PaperTradeRequest;
import com.salihguneyin.papertradehub.dto.PaperTradeResponse;
import com.salihguneyin.papertradehub.service.TradeService;
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
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping
    public List<PaperTradeResponse> getAll() {
        return tradeService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaperTradeResponse create(@Valid @RequestBody PaperTradeRequest request) {
        return tradeService.create(request);
    }
}

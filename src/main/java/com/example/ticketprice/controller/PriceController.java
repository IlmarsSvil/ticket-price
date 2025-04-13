package com.example.ticketprice.controller;

import com.example.ticketprice.model.dto.PriceRequest;
import com.example.ticketprice.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @Operation(summary = "Calculate provisional ticket prices for all passengers together")
    @PostMapping("/calculate-price")
    public String calculatePrice(@RequestBody PriceRequest request) {
        return priceService.calculatePrice(request);
    }
}

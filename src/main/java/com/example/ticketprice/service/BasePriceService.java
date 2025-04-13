package com.example.ticketprice.service;

import com.example.ticketprice.model.BasePrice;
import com.example.ticketprice.repository.BasePriceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BasePriceService {

    private final BasePriceRepository basePriceRepository;

    public BasePriceService(BasePriceRepository basePriceRepository) {
        this.basePriceRepository = basePriceRepository;
    }

    public List<BasePrice> getAllBasePrices() {
        return basePriceRepository.findAll();
    }

    public Optional<BasePrice> getBasePriceById(Long id) {
        return basePriceRepository.findById(id);
    }

    public BasePrice getByTerminalName(String terminalName) {
        return basePriceRepository.findByTerminalName(terminalName);
    }
}

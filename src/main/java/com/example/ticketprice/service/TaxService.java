package com.example.ticketprice.service;

import com.example.ticketprice.model.Tax;
import com.example.ticketprice.repository.TaxRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaxService {

    private final TaxRepository taxRepository;

    public TaxService(TaxRepository taxRepository) {
        this.taxRepository = taxRepository;
    }

    public List<Tax> getAllTaxes() {
        return taxRepository.findAll();
    }

    public Optional<Tax> getTaxById(Long id) {
        return taxRepository.findById(id);
    }

    public Tax findByDate(LocalDate date) {
        return taxRepository.findByTaxDate(date);
    }
}

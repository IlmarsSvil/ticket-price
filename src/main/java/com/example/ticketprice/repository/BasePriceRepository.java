package com.example.ticketprice.repository;

import com.example.ticketprice.model.BasePrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasePriceRepository extends JpaRepository<BasePrice, Long> {
    BasePrice findByTerminalName(String terminalName);
}
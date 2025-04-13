package com.example.ticketprice.repository;

import com.example.ticketprice.model.Tax;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface TaxRepository extends JpaRepository<Tax, Long> {
    Tax findByTaxDate(LocalDate taxDate);
}
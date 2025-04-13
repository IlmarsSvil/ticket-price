package com.example.ticketprice.service;

import com.example.ticketprice.model.BasePrice;
import com.example.ticketprice.model.Tax;
import com.example.ticketprice.model.dto.PriceRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PriceService {
    private final BasePriceService basePriceService;
    private final TaxService taxService;


    public PriceService(BasePriceService basePriceService, TaxService taxService) {
        this.basePriceService = basePriceService;
        this.taxService = taxService;
    }

    public BigDecimal calculatePrice(PriceRequest request) {
        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO); // Use AtomicReference to hold total

        BigDecimal taxRate = Optional.ofNullable(taxService.findByDate(LocalDate.now()))
                .map(Tax::getTaxRate)
                .map(rate -> rate.divide(BigDecimal.valueOf(100)))
                .orElseThrow(() -> new IllegalStateException("Tax not found or invalid rate"));

        request.getPassengers().forEach(passenger -> {
            //Base Price and bag count of the Ticket
            BigDecimal basePrice = Optional.ofNullable(
                            basePriceService.getByTerminalName(passenger.getTerminal())
                    ).map(BasePrice::getBasePrice)
                    .orElseThrow(() -> new IllegalStateException("Base price for terminal or terminal not found or invalid: " + passenger.getTerminal()));


            BigDecimal bagCount = Optional.of(passenger.getBags())
                    .filter(bagCountValue -> bagCountValue >= 0)  // Optional filter for invalid values
                    .map(BigDecimal::valueOf)
                    .orElseThrow(() -> new IllegalArgumentException("Bag count cannot be null or negative"));

            //Person Ticket**
            BigDecimal ticketPrice = addKidsDiscount(basePrice, passenger.getAge());
            ticketPrice = addTax(ticketPrice, taxRate);

            //Baggage**
            BigDecimal baggageCost = basePrice.multiply(bagCount).multiply(BigDecimal.valueOf(0.3));
            //add pvn to baggage
            BigDecimal baggageWithPVN = addTax(baggageCost, taxRate);


            //add together ticket+baggage
            ticketPrice = ticketPrice.add(baggageWithPVN);
            //add this individual persons ticket Price to total
            total.set(total.get().add(ticketPrice));
        });
        return total.get().setScale(2, RoundingMode.HALF_UP);
    }

    //50% discount if under 18
    public BigDecimal addKidsDiscount(BigDecimal base, Integer age) {
        if (age != null && age < 18) {
            return base.multiply(BigDecimal.valueOf(0.5));
        }
        return base;
    }

    //returns base if tax is not above 0
    public BigDecimal addTax(BigDecimal base, BigDecimal tax) {
        if (tax.compareTo(BigDecimal.ZERO) > 0) {
            return base.add(base.multiply(tax));
        }
        return base;
    }
}


package com.example.ticketprice.service;

import com.example.ticketprice.model.BasePrice;
import com.example.ticketprice.model.Tax;
import com.example.ticketprice.model.dto.PriceRequest;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PriceService {
    private final BasePriceService basePriceService;
    private final TaxService taxService;
    private final MessageSource messageSource;
    private final MeterRegistry meterRegistry;

    public PriceService(BasePriceService basePriceService, TaxService taxService, MessageSource messageSource, MeterRegistry meterRegistry) {
        this.basePriceService = basePriceService;
        this.taxService = taxService;
        this.messageSource = messageSource;
        this.meterRegistry = meterRegistry;
    }

    public String calculatePrice(PriceRequest request) {
        StringBuilder result = new StringBuilder();
        meterRegistry.counter("call.price.calculation.api.counter").increment();
        result.append(messageSource.getMessage("ticketPrice", null, Locale.getDefault()));
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
                    .filter(bagCountValue -> bagCountValue >= 0)
                    .map(BigDecimal::valueOf)
                    .orElseThrow(() -> new IllegalArgumentException("Bag count cannot be null or negative"));


            //Person Ticket**
            BigDecimal ticketPrice = addKidsDiscount(basePrice, passenger.getAge());
            ticketPrice = addTax(ticketPrice, taxRate);
            if (passenger.getAge() > 18) {
                result.append(messageSource.getMessage("adult", new Object[]{String.format("%.2f", ticketPrice.setScale(2, RoundingMode.HALF_UP))}, Locale.getDefault()));
            } else {
                result.append(messageSource.getMessage("kid", new Object[]{String.format("%.2f", ticketPrice.setScale(2, RoundingMode.HALF_UP))}, Locale.getDefault()));
            }

            //Baggage**
            BigDecimal baggageCost = basePrice.multiply(bagCount).multiply(BigDecimal.valueOf(0.3));
            BigDecimal baggageWithPVN = addTax(baggageCost, taxRate);
            if (bagCount.compareTo(BigDecimal.ONE) > 0) {
                result.append(messageSource.getMessage("multipleBags", new Object[]{bagCount, String.format("%.2f", baggageWithPVN.setScale(2, RoundingMode.HALF_UP))}, Locale.getDefault()));
            } else {
                result.append(messageSource.getMessage("bag", new Object[]{bagCount, String.format("%.2f", baggageWithPVN.setScale(2, RoundingMode.HALF_UP))}, Locale.getDefault()));
            }

            //add together ticket+baggage
            ticketPrice = ticketPrice.add(baggageWithPVN);

            //add this individual persons ticket Price to total
            total.set(total.get().add(ticketPrice));
        });
        result.append(messageSource.getMessage("result", new Object[]{String.format("%.2f", total.get().setScale(2, RoundingMode.HALF_UP))}, Locale.getDefault()));

        meterRegistry.counter("done.price.calculation.api.counter", "status", "success").increment();
        return result.toString();
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


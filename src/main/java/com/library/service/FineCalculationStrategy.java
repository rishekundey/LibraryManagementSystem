package main.java.com.library.service;

import java.time.LocalDate;

// Strategy Interface
public interface FineCalculationStrategy {
    double calculateFine(LocalDate checkoutDate, LocalDate returnDate);
}
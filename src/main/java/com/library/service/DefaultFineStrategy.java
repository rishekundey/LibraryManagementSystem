package main.java.com.library.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Concrete Strategy: $10/day after 14 days from checkout
public class DefaultFineStrategy implements FineCalculationStrategy {
    private static final int FINE_FREE_DAYS = 14;
    private static final double FINE_PER_DAY = 10.0;

    @Override
    public double calculateFine(LocalDate checkoutDate, LocalDate returnDate) {
        if (returnDate == null) return 0.0;

        long daysOverdue = ChronoUnit.DAYS.between(checkoutDate.plusDays(FINE_FREE_DAYS), returnDate);

        if (daysOverdue > 0) {
            return daysOverdue * FINE_PER_DAY;
        }
        return 0.0;
    }
}
package ca.seneca.apd545.RXHgrandhotel.service;

import java.time.LocalDate;

public interface PricingStrategy {
    double calculateNightlyRate(double baseRate, LocalDate date);
}

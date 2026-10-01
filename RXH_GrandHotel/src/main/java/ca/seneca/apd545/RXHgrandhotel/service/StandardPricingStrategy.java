package ca.seneca.apd545.RXHgrandhotel.service;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class StandardPricingStrategy implements PricingStrategy {

    private final double weekdayMultiplier;
    private final double weekendMultiplier;

    public StandardPricingStrategy(double weekdayMultiplier, double weekendMultiplier) {
        this.weekdayMultiplier = weekdayMultiplier;
        this.weekendMultiplier = weekendMultiplier;
    }

    @Override
    public double calculateNightlyRate(double baseRate, LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        boolean weekend = (dow == DayOfWeek.FRIDAY || dow == DayOfWeek.SATURDAY);
        return baseRate * (weekend ? weekendMultiplier : weekdayMultiplier);
    }
}

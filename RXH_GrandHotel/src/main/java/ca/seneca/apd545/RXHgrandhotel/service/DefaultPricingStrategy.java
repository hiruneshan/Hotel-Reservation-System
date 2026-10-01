package ca.seneca.apd545.RXHgrandhotel.service;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DefaultPricingStrategy implements PricingStrategy {

    @Override
    public double calculateNightlyRate(double baseRate, LocalDate date) {

        double rate = baseRate;


        DayOfWeek day = date.getDayOfWeek();
        if (day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY) {
            rate *= 1.20;
        }


        if (date.getMonthValue() == 12 && date.getDayOfMonth() == 25) {
            rate *= 1.50;
        }

        return rate;
    }
}

package ca.seneca.apd545.RXHgrandhotel.service;

import ca.seneca.apd545.RXHgrandhotel.model.Room;
import ca.seneca.apd545.RXHgrandhotel.model.enums.RoomType;

import java.util.List;
import java.util.Map;
import java.util.EnumMap;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PricingService {

    private final PricingStrategy strategy;


    private final Map<RoomType, Double> basePrices = new EnumMap<>(RoomType.class);

    public PricingService(PricingStrategy strategy) {
        this.strategy = strategy;


        basePrices.put(RoomType.SINGLE, 100.0);
        basePrices.put(RoomType.DOUBLE, 150.0);
        basePrices.put(RoomType.SUITE, 350.0);
    }
    public double calculateRoomsSubtotal(List<Room> rooms, LocalDate checkIn, LocalDate checkOut) {
        if (rooms == null || checkIn == null || checkOut == null) {
            return 0.0;
        }


        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) nights = 1;

        double subtotal = 0.0;
        for (Room room : rooms) {
            subtotal += (room.getBasePrice() * nights);
        }

        return subtotal;
    }


    public double getBasePrice(RoomType type) {
        return basePrices.getOrDefault(type, 100.0);
    }


    public double calculateStayPrice(double baseRate, LocalDate checkIn, LocalDate checkOut) {
        double total = 0;
        LocalDate date = checkIn;

        while (date.isBefore(checkOut)) {
            total += strategy.calculateNightlyRate(baseRate, date);
            date = date.plusDays(1);
        }

        return total;
    }
}

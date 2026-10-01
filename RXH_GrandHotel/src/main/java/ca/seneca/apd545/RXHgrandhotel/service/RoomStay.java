package ca.seneca.apd545.RXHgrandhotel.service;

// The base room charge
public class RoomStay implements Billable {
    private double basePrice;
    private String roomType;

    public RoomStay(double basePrice, String roomType) {
        this.basePrice = basePrice;
        this.roomType = roomType;
    }

    @Override
    public double getPrice() { return basePrice; }

    @Override
    public String getDescription() { return roomType + " Room Stay"; }
}


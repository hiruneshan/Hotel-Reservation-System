package ca.seneca.apd545.RXHgrandhotel.service;

public class StandardLoyaltyStrategy implements LoyaltyStrategy {
    private final double earningRate = 1.0;
    private final double pointValue = 0.05;

    @Override
    public int calculatePointsEarned(double amount) {
        return (int) (amount * earningRate);
    }

    @Override
    public double calculateDiscountValue(int points) {
        return points * pointValue;
    }
}
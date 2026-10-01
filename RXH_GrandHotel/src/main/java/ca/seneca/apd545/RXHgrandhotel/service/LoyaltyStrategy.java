package ca.seneca.apd545.RXHgrandhotel.service;

public interface LoyaltyStrategy {

    int calculatePointsEarned(double paymentAmount);


    double calculateDiscountValue(int pointsToRedeem);
}
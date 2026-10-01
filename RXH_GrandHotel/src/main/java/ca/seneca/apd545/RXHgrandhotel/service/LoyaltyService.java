package ca.seneca.apd545.RXHgrandhotel.service;

import ca.seneca.apd545.RXHgrandhotel.model.Guest;
import ca.seneca.apd545.RXHgrandhotel.model.LoyaltyAccount;
import ca.seneca.apd545.RXHgrandhotel.repository.LoyaltyRepository;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;
import java.util.Optional;

public class LoyaltyService {

    private final LoyaltyRepository loyaltyRepository;
    private final LoyaltyStrategy loyaltyStrategy;


    public LoyaltyService(LoyaltyRepository loyaltyRepository, LoyaltyStrategy loyaltyStrategy) {
        this.loyaltyRepository = loyaltyRepository;
        this.loyaltyStrategy = loyaltyStrategy;
    }


    public void earnPoints(Long accountId, double amountPaid) {
        loyaltyRepository.findById(accountId).ifPresent(account -> {
            int earned = loyaltyStrategy.calculatePointsEarned(amountPaid);
            account.setPoints(account.getPoints() + earned);
            loyaltyRepository.save(account);

            // Requirement: Log administrative actions and payments
            LoggerUtil.info("LOYALTY_EARNED - Account: " + account.getLoyaltyNumber() +
                    " | Points Added: " + earned + " | New Balance: " + account.getPoints());
        });
    }


    public double redeemPoints(Long accountId, int pointsToRedeem) {
        LoyaltyAccount account = loyaltyRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Loyalty account not found"));

        if (account.getPoints() < pointsToRedeem) {
            throw new IllegalArgumentException("Insufficient points balance for redemption.");
        }

        double discount = loyaltyStrategy.calculateDiscountValue(pointsToRedeem);
        account.setPoints(account.getPoints() - pointsToRedeem);
        loyaltyRepository.save(account);
        LoggerUtil.info("LOYALTY_REDEMPTION - Account: " + account.getLoyaltyNumber() +
                " | Points Used: " + pointsToRedeem + " | Discount Applied: $" + discount);

        return discount;
    }

    public LoyaltyAccount registerGuest(Guest guest) {
        LoyaltyAccount account = new LoyaltyAccount();
        account.setGuest(guest);
        account.setPoints(0);
        account.setLoyaltyNumber("L-" + System.currentTimeMillis());
        account.setFullName(guest.getFullName());
        account.setPhone(guest.getPhone());
        account.setEmail(guest.getEmail());
        account.setAddress(guest.getAddress());
        account.setCity(guest.getCity());
        account.setProvince(guest.getProvince());
        account.setPostal(guest.getPostal());
        account.setCountry(guest.getCountry());
        account.setDob(guest.getDob());

        LoggerUtil.info("LOYALTY_ENROLLED - Guest: " + guest.getFullName());
        return loyaltyRepository.save(account);
    }

    public Optional<LoyaltyAccount> findByLoyaltyNumber(String number) {
        return loyaltyRepository.findByLoyaltyNumber(number);
    }
    public LoyaltyAccount findAccount(String name, String loyaltyNumber) {
        if (loyaltyNumber != null && !loyaltyNumber.isEmpty()) {
            return loyaltyRepository.findByLoyaltyNumber(loyaltyNumber).orElse(null);
        }

        if (name != null && !name.isEmpty()) {

            return loyaltyRepository.findAll().stream()
                    .filter(acc -> acc.getGuest().getFullName().toLowerCase().contains(name.toLowerCase()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public Optional<LoyaltyAccount> findAccountByGuestId(Long guestId) {
        return loyaltyRepository.findAll().stream()
                .filter(acc -> acc.getGuest().getId().equals(guestId))
                .findFirst();
    }


    public java.util.List<ca.seneca.apd545.RXHgrandhotel.model.LoyaltyHistory> getHistoryForAccount(Long accountId) {

        return loyaltyRepository.findHistoryByAccountId(accountId);
    }
}
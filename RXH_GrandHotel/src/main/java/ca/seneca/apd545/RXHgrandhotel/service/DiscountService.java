package ca.seneca.apd545.RXHgrandhotel.service;

import ca.seneca.apd545.RXHgrandhotel.model.Discount;
import ca.seneca.apd545.RXHgrandhotel.repository.DiscountRepository;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;
import java.util.List;

public class DiscountService {

    private final DiscountRepository discountRepo;


    public DiscountService(DiscountRepository discountRepo) {
        this.discountRepo = discountRepo;
    }

    public Discount save(Discount discount, String userRole) {

        double limit = (userRole != null && userRole.equalsIgnoreCase("MANAGER")) ? 30.0 : 15.0;

        if (discount.getPercentage() > limit) {
            throw new IllegalArgumentException("Permission Denied: Discount exceeds the " + limit + "% limit for " + userRole);
        }

        if (discount.getPercentage() < 0) {
            throw new IllegalArgumentException("Discount value cannot be negative.");
        }

        Discount saved = discountRepo.save(discount);

        LoggerUtil.info("DISCOUNT_SAVED - ID: " + saved.getId() +
                " | Value: " + saved.getPercentage() +
                "% | Role: " + userRole +
                " | Reason: " + saved.getReason());

        return saved;
    }

    public List<Discount> getAllSeasonalDiscounts() {
        return discountRepo.findAll();
    }

    public void delete(Discount discount) {
        discountRepo.delete(discount);
        LoggerUtil.info("DISCOUNT_DELETED - ID: " + discount.getId());
    }
}
package ca.seneca.apd545.RXHgrandhotel.repository;

import ca.seneca.apd545.RXHgrandhotel.model.Discount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.List;

/**
 * Requirement: Repositories must be ORM-backed[cite: 32].
 * Requirement: Use constructor injection for the EntityManagerFactory[cite: 150].
 */
public class DiscountRepository {

    private final EntityManagerFactory emf;

    public DiscountRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Discount save(Discount discount) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (discount.getId() == null) {
                em.persist(discount); // Create new
            } else {
                discount = em.merge(discount); // Update existing
            }
            tx.commit();
            return discount;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close(); // Requirement: EntityManager must not be shared across threads [cite: 145]
        }
    }

    public List<Discount> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            // Requirement: Use JPA queries for persistence operations [cite: 147]
            return em.createQuery("SELECT d FROM Discount d", Discount.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(Discount discount) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Discount managedDiscount = em.find(Discount.class, discount.getId());
            if (managedDiscount != null) {
                em.remove(managedDiscount);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Requirement: Support filtering by date range for seasonal multipliers [cite: 197, 200]
     */
    public List<Discount> findActiveSeasonalDiscounts() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT d FROM Discount d WHERE d.startDate <= :today AND d.endDate >= :today",
                            Discount.class)
                    .setParameter("today", java.time.LocalDate.now())
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
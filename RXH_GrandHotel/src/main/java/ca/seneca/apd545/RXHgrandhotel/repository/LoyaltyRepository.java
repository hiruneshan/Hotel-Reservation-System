package ca.seneca.apd545.RXHgrandhotel.repository;

import ca.seneca.apd545.RXHgrandhotel.model.LoyaltyAccount;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class LoyaltyRepository {

    private final EntityManagerFactory emf;

    public LoyaltyRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private EntityManager em() {
        return emf.createEntityManager();
    }

    public LoyaltyAccount save(LoyaltyAccount account) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            if (account.getId() == null) {
                em.persist(account);
            } else {
                account = em.merge(account);
            }
            em.getTransaction().commit();
            return account;
        } finally {
            em.close();
        }
    }

    public Optional<LoyaltyAccount> findById(Long id) {
        EntityManager em = em();
        try {
            return Optional.ofNullable(em.find(LoyaltyAccount.class, id));
        } finally {
            em.close();
        }
    }

    public Optional<LoyaltyAccount> findByLoyaltyNumber(String loyaltyNumber) {
        EntityManager em = em();
        try {
            TypedQuery<LoyaltyAccount> query = em.createQuery(
                    "SELECT l FROM LoyaltyAccount l WHERE l.loyaltyNumber = :num",
                    LoyaltyAccount.class
            );
            query.setParameter("num", loyaltyNumber);
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public List<ca.seneca.apd545.RXHgrandhotel.model.LoyaltyHistory> findHistoryByAccountId(Long accountId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ca.seneca.apd545.RXHgrandhotel.model.LoyaltyHistory> query = em.createQuery(
                    "SELECT h FROM LoyaltyHistory h WHERE h.account.id = :accId ORDER BY h.date DESC",
                    ca.seneca.apd545.RXHgrandhotel.model.LoyaltyHistory.class
            );
            query.setParameter("accId", accountId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<LoyaltyAccount> findByPhone(String phone) {
        EntityManager em = em();
        try {
            TypedQuery<LoyaltyAccount> query = em.createQuery(
                    "SELECT l FROM LoyaltyAccount l WHERE l.guest.phone = :phone",
                    LoyaltyAccount.class
            );
            query.setParameter("phone", phone);
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public List<LoyaltyAccount> findAll() {
        EntityManager em = em();
        try {
            TypedQuery<LoyaltyAccount> query = em.createQuery(
                    "SELECT l FROM LoyaltyAccount l",
                    LoyaltyAccount.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

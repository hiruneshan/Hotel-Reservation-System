package ca.seneca.apd545.RXHgrandhotel.repository;

import ca.seneca.apd545.RXHgrandhotel.model.AdminUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import java.util.Optional;

public class AdminUserRepository {

    private final EntityManagerFactory emf;

    public AdminUserRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    //finding by user name
    public Optional<AdminUser> findByUsername(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            AdminUser user = em.createQuery(
                            "SELECT a FROM AdminUser a WHERE a.username = :username AND a.isActive = true",
                            AdminUser.class)
                    .setParameter("username", username.trim().toLowerCase())
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public AdminUser save(AdminUser user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            AdminUser saved = em.merge(user);
            em.getTransaction().commit();
            return saved;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
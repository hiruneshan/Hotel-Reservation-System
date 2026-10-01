package ca.seneca.apd545.RXHgrandhotel.repository;

import ca.seneca.apd545.RXHgrandhotel.model.WaitlistEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class WaitlistRepository {

    private final EntityManagerFactory emf;

    public WaitlistRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private EntityManager em() {
        return emf.createEntityManager();
    }

    public WaitlistEntry save(WaitlistEntry entry) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            if (entry.getId() == null) {
                em.persist(entry); // New entry
            } else {
                entry = em.merge(entry);
            }
            em.getTransaction().commit();
            return entry;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Optional<WaitlistEntry> findById(int id) {
        EntityManager em = em();
        try {
            return Optional.ofNullable(em.find(WaitlistEntry.class, id));
        } finally {
            em.close();
        }
    }

    public List<WaitlistEntry> findAll() {
        EntityManager em = em();
        try {
            TypedQuery<WaitlistEntry> query = em.createQuery(
                    "SELECT w FROM WaitlistEntry w",
                    WaitlistEntry.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(WaitlistEntry entry) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            WaitlistEntry mergedEntry = em.merge(entry);
            em.remove(mergedEntry);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}

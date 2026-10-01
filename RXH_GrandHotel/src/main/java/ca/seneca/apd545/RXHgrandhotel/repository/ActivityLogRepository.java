package ca.seneca.apd545.RXHgrandhotel.repository;

import ca.seneca.apd545.RXHgrandhotel.model.ActivityLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class ActivityLogRepository {
    private final EntityManagerFactory emf;

    public ActivityLogRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void save(ActivityLog log) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(log);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<ActivityLog> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM ActivityLog a ORDER BY a.timestamp DESC", ActivityLog.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
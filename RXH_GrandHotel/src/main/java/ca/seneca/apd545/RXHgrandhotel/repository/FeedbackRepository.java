package ca.seneca.apd545.RXHgrandhotel.repository;

import ca.seneca.apd545.RXHgrandhotel.model.Feedback;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class FeedbackRepository {

    private final EntityManagerFactory emf;

    public FeedbackRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private EntityManager em() {
        return emf.createEntityManager();
    }

    public Feedback save(Feedback feedback) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            if (feedback.getId() == null) {
                em.persist(feedback);
            } else {
                feedback = em.merge(feedback);
            }
            em.getTransaction().commit();
            return feedback;
        } finally {
            em.close();
        }
    }

    public List<Feedback> findByRating(int rating) {
        EntityManager em = em();
        try {
            TypedQuery<Feedback> query = em.createQuery(
                    "SELECT f FROM Feedback f WHERE f.rating = :rating",
                    Feedback.class
            );
            query.setParameter("rating", rating);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Feedback> findByDateRange(java.time.LocalDate start, java.time.LocalDate end) {
        EntityManager em = em();
        try {
            TypedQuery<Feedback> query = em.createQuery(
                    "SELECT f FROM Feedback f WHERE f.date BETWEEN :start AND :end",
                    Feedback.class
            );
            query.setParameter("start", start);
            query.setParameter("end", end);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Feedback> findById(Long id) {
        EntityManager em = em();
        try {
            return Optional.ofNullable(em.find(Feedback.class, id));
        } finally {
            em.close();
        }
    }

    public List<Feedback> findAll() {
        EntityManager em = em();
        try {
            TypedQuery<Feedback> query = em.createQuery(
                    "SELECT f FROM Feedback f",
                    Feedback.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

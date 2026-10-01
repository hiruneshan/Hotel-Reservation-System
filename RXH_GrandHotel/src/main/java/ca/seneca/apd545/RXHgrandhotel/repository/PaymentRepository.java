package ca.seneca.apd545.RXHgrandhotel.repository;

import ca.seneca.apd545.RXHgrandhotel.model.Payment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class PaymentRepository {

    private final EntityManagerFactory emf;

    public PaymentRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private EntityManager em() {
        return emf.createEntityManager();
    }

    public Payment save(Payment payment) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            if (payment.getId() == null) {
                em.persist(payment);
            } else {
                payment = em.merge(payment);
            }
            em.getTransaction().commit();
            return payment;
        } finally {
            em.close();
        }
    }

    public Optional<Payment> findById(Long id) {
        EntityManager em = em();
        try {
            return Optional.ofNullable(em.find(Payment.class, id));
        } finally {
            em.close();
        }
    }

    public List<Payment> findByReservationId(Long reservationId) {
        EntityManager em = em();
        try {
            TypedQuery<Payment> query = em.createQuery(
                    "SELECT p FROM Payment p WHERE p.reservation.id = :resId",
                    Payment.class
            );
            query.setParameter("resId", reservationId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

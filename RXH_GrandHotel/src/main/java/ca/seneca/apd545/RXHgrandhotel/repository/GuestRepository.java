package ca.seneca.apd545.RXHgrandhotel.repository;

import ca.seneca.apd545.RXHgrandhotel.model.Guest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class GuestRepository {

    private final EntityManagerFactory emf;

    public GuestRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private EntityManager em() {
        return emf.createEntityManager();
    }

    public Guest save(Guest guest) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            if (guest.getId() == null) {
                em.persist(guest);
            } else {
                guest = em.merge(guest);
            }
            em.getTransaction().commit();
            return guest;
        } finally {
            em.close();
        }
    }

    public Optional<Guest> findById(Long id) {
        EntityManager em = em();
        try {
            return Optional.ofNullable(em.find(Guest.class, id));
        } finally {
            em.close();
        }
    }

    public Optional<Guest> findByPhone(String phone) {
        EntityManager em = em();
        try {
            TypedQuery<Guest> query = em.createQuery(
                    "SELECT g FROM Guest g WHERE g.phone = :phone",
                    Guest.class
            );
            query.setParameter("phone", phone);
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public List<Guest> findAll() {
        EntityManager em = em();
        try {
            TypedQuery<Guest> query = em.createQuery(
                    "SELECT g FROM Guest g",
                    Guest.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

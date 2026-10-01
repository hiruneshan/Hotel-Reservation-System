package ca.seneca.apd545.RXHgrandhotel.repository;

import ca.seneca.apd545.RXHgrandhotel.model.Reservation;
import ca.seneca.apd545.RXHgrandhotel.model.enums.RoomType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ReservationRepository {

    private final EntityManagerFactory emf;

    public ReservationRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private EntityManager em() {
        return emf.createEntityManager();
    }

    public Reservation save(Reservation reservation) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            if (reservation.getId() == null) {
                em.persist(reservation);
            } else {
                reservation = em.merge(reservation);
            }
            em.getTransaction().commit();
            return reservation;
        } finally {
            em.close();
        }
    }

    public long countOverlappingReservations(RoomType type, LocalDate start, LocalDate end) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT COUNT(r) FROM Reservation r " +
                    "JOIN r.rooms rr " +
                    "WHERE rr.room.type = :type " +
                    "AND r.checkIn < :end " +
                    "AND r.checkOut > :start " +
                    "AND r.status != 'CANCELLED'";

            return em.createQuery(jpql, Long.class)
                    .setParameter("type", type)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }


    public Optional<Reservation> findById(Long id) {
        EntityManager em = em();
        try {
            return Optional.ofNullable(em.find(Reservation.class, id));
        } finally {
            em.close();
        }
    }

    public List<Reservation> findAll() {
        EntityManager em = em();
        try {
            TypedQuery<Reservation> query = em.createQuery(
                    "SELECT r FROM Reservation r",
                    Reservation.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Reservation> findByDateRange(LocalDate start, LocalDate end) {
        EntityManager em = em();
        try {
            TypedQuery<Reservation> query = em.createQuery(
                    "SELECT r FROM Reservation r " +
                            "WHERE r.checkIn <= :endDate AND r.checkOut >= :startDate",
                    Reservation.class
            );
            query.setParameter("startDate", start);
            query.setParameter("endDate", end);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Integer> findReservedRoomIds(String roomType, LocalDate checkIn, LocalDate checkOut) {
        EntityManager em = em();
        try {
            TypedQuery<Integer> query = em.createQuery(
                    "SELECT rr.room.id FROM ReservationRoom rr " +
                            "WHERE rr.roomType = :roomType " +
                            "AND rr.reservation.checkIn < :checkOut " +
                            "AND rr.reservation.checkOut > :checkIn",
                    Integer.class
            );
            query.setParameter("roomType", roomType);
            query.setParameter("checkIn", checkIn);
            query.setParameter("checkOut", checkOut);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}

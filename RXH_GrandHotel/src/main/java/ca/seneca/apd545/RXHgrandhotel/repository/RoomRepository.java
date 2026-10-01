package ca.seneca.apd545.RXHgrandhotel.repository;

import ca.seneca.apd545.RXHgrandhotel.model.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RoomRepository {

    private final EntityManagerFactory emf;

    public RoomRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private EntityManager em() {
        return emf.createEntityManager();
    }

    public Room save(Room room) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            if (room.getId() == null) {
                em.persist(room);
            } else {
                room = em.merge(room);
            }
            em.getTransaction().commit();
            return room;
        } finally {
            em.close();
        }
    }

    public Optional<Room> findById(Long id) {
        EntityManager em = em();
        try {
            return Optional.ofNullable(em.find(Room.class, id));
        } finally {
            em.close();
        }
    }

    public List<Room> findAll() {
        EntityManager em = em();
        try {
            TypedQuery<Room> query = em.createQuery(
                    "SELECT r FROM Room r",
                    Room.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Room> findByType(String type) {
        EntityManager em = em();
        try {
            TypedQuery<Room> query = em.createQuery(
                    "SELECT r FROM Room r WHERE r.type = :type",
                    Room.class
            );
            query.setParameter("type", type);
            return query.getResultList();
        } finally {
            em.close();
        }
    }


    public List<Integer> findRoomIdsByType(String type) {
        EntityManager em = em();
        try {
            TypedQuery<Integer> query = em.createQuery(
                    "SELECT r.id FROM Room r WHERE r.type = :type",
                    Integer.class
            );
            query.setParameter("type", type);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        EntityManager em = em();
        try {
            return em.createQuery(
                            "SELECT r FROM Room r WHERE r.id NOT IN (" +
                                    "SELECT rr.room.id FROM ReservationRoom rr " +
                                    "WHERE rr.reservation.checkIn < :checkOut " +
                                    "AND rr.reservation.checkOut > :checkIn " +
                                    "AND rr.reservation.status != 'CANCELLED')", Room.class)
                    .setParameter("checkIn", checkIn)
                    .setParameter("checkOut", checkOut)
                    .getResultList();
        } finally {
            em.close();
        }
    }


}

package ca.seneca.apd545.RXHgrandhotel.service;

import ca.seneca.apd545.RXHgrandhotel.events.AdminObserver;
import ca.seneca.apd545.RXHgrandhotel.model.Reservation;
import ca.seneca.apd545.RXHgrandhotel.model.enums.ReservationStatus;
import ca.seneca.apd545.RXHgrandhotel.model.enums.RoomType;
import ca.seneca.apd545.RXHgrandhotel.repository.ReservationRepository;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final List<AdminObserver> observers = new ArrayList<>();


    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }


    public void addObserver(AdminObserver observer) {
        observers.add(observer);
    }

    public Reservation save(Reservation reservation) {

        return reservationRepository.save(reservation);
    }

    public Reservation findById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }


    public List<Reservation> searchReservations(String name, String phone, ReservationStatus status, LocalDate start, LocalDate end) {
        List<Reservation> all = reservationRepository.findAll();

        return all.stream()
                .filter(r -> (name == null || name.isEmpty() ||
                        r.getGuest().getFullName().toLowerCase().contains(name.toLowerCase())))
                .filter(r -> (phone == null || phone.isEmpty() ||
                        r.getGuest().getPhone().contains(phone)))
                .filter(r -> (status == null || r.getStatus() == status))
                .filter(r -> (start == null || !r.getCheckIn().isBefore(start)))
                .filter(r -> (end == null || !r.getCheckOut().isAfter(end)))
                .collect(Collectors.toList());
    }


    public boolean checkAvailability(RoomType type, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || checkIn.isAfter(checkOut)) {
            return false;
        }

        long overlappingCount = reservationRepository.countOverlappingReservations(type, checkIn, checkOut);


        int totalRoomsOfType = 10;

        return overlappingCount < totalRoomsOfType;
    }

    public void checkout(Long reservationId) {
        Reservation res = findById(reservationId);

        if (res != null) {

            double balance = res.getTotalAmount() - res.getPayments().stream().mapToDouble(p -> p.getAmount()).sum();
            if (balance > 0.01) {
                throw new IllegalStateException("Cannot checkout: Outstanding balance of $" + balance);
            }

            res.setStatus(ReservationStatus.CHECKED_OUT);
            reservationRepository.save(res);

            LoggerUtil.info("SYSTEM", "CHECKOUT", "Reservation", reservationId.toString(), "Guest checked out successfully.");

            notifyObservers(res);
        }
    }

    private void notifyObservers(Reservation res) {
        res.getRooms().forEach(resRoom -> {
            for (AdminObserver observer : observers) {
                observer.onRoomAvailable(
                        resRoom.getRoom().getRoomNumber(),
                        resRoom.getRoom().getType().toString()
                );
            }
        });
    }
}
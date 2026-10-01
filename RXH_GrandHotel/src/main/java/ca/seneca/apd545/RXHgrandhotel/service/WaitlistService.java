package ca.seneca.apd545.RXHgrandhotel.service;

import ca.seneca.apd545.RXHgrandhotel.model.Reservation;
import ca.seneca.apd545.RXHgrandhotel.model.Room;
import ca.seneca.apd545.RXHgrandhotel.model.WaitlistEntry;
import ca.seneca.apd545.RXHgrandhotel.model.enums.ReservationStatus;
import ca.seneca.apd545.RXHgrandhotel.repository.RoomRepository;
import ca.seneca.apd545.RXHgrandhotel.repository.WaitlistRepository;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;
import java.util.List;

public class WaitlistService {

    private final WaitlistRepository waitlistRepo;
    private final ReservationService reservationService;
    private final RoomRepository roomRepository;
    private final PricingService pricingService;

    public WaitlistService(WaitlistRepository waitlistRepo,
                           ReservationService reservationService,
                           PricingService pricingService,
                           RoomRepository roomRepository) {
        this.waitlistRepo = waitlistRepo;
        this.reservationService = reservationService;
        this.pricingService = pricingService;
        this.roomRepository = roomRepository;
    }

    public List<WaitlistEntry> getAllActiveEntries() {
        return waitlistRepo.findAll();
    }

    public WaitlistEntry saveEntry(WaitlistEntry entry) {

        return waitlistRepo.save(entry);
    }

    public void convertToReservation(WaitlistEntry entry) throws Exception {

        List<Room> availableRooms = roomRepository.findAvailableRooms(
                entry.getCheckIn(),
                entry.getCheckOut()
        );

        Room assignedRoom = availableRooms.stream()
                .filter(r -> r.getType() == entry.getRoomType())
                .findFirst()
                .orElseThrow(() -> new Exception("No rooms of type " + entry.getRoomType() + " available for these dates."));

        Reservation reservation = new Reservation();
        reservation.setGuest(entry.getGuest());
        reservation.setCheckIn(entry.getCheckIn());
        reservation.setCheckOut(entry.getCheckOut());
        reservation.setStatus(ReservationStatus.BOOKED);


        reservation.addRoom(assignedRoom);

        double subtotal = pricingService.calculateRoomsSubtotal(
                java.util.List.of(assignedRoom),
                entry.getCheckIn(),
                entry.getCheckOut()
        );

        double tax = subtotal * 0.13;
        reservation.setSubtotal(subtotal);
        reservation.setTax(tax);
        reservation.setTotalAmount(subtotal + tax);
        reservationService.save(reservation);
        waitlistRepo.delete(entry);

        LoggerUtil.info("ADMIN", "WAITLIST_CONVERT", "WaitlistEntry",
                String.valueOf(entry.getId()), "Converted to Reservation with Total: $" + reservation.getTotalAmount());
    }
}
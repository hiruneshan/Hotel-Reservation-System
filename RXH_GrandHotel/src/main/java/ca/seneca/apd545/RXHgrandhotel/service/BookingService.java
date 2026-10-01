package ca.seneca.apd545.RXHgrandhotel.service;

import ca.seneca.apd545.RXHgrandhotel.model.*;
import ca.seneca.apd545.RXHgrandhotel.model.enums.ReservationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BookingService {

    private final ReservationService reservationService;
    private final GuestService guestService;


    private final PricingService pricingService;


    public BookingService(ReservationService reservationService,
                          GuestService guestService,
                          PricingService pricingService) {
        this.reservationService = reservationService;
        this.guestService = guestService;
        this.pricingService = pricingService;
    }


    public double calculateRoomsSubtotal(List<Room> rooms, LocalDate checkIn, LocalDate checkOut) {
        double subtotal = 0;
        for (Room room : rooms) {
            double base = pricingService.getBasePrice(room.getType());
            subtotal += pricingService.calculateStayPrice(base, checkIn, checkOut);
        }
        return subtotal;
    }


    public Reservation createReservation(
            Guest guest,
            List<ReservationRoom> rooms,
            LocalDate checkIn,
            LocalDate checkOut,
            int adults,
            int children) {

        validateDates(checkIn, checkOut);

        Guest savedGuest = guestService.save(guest);

        Reservation reservation = new Reservation();
        reservation.setGuest(savedGuest);
        reservation.setCheckIn(checkIn);
        reservation.setCheckOut(checkOut);
        reservation.setAdults(adults);
        reservation.setChildren(children);
        reservation.setStatus(ReservationStatus.BOOKED);

        reservation.getRooms().addAll(rooms);

        calculateAndSetTotals(reservation);

        return reservationService.save(reservation);
    }


    public void createAdminReservation(String name, String phone,
                                       String email, String address, String city,
                                       String province, String postal, String country,
                                       LocalDate in, LocalDate out,
                                       int adults, int children,
                                       List<Room> selectedRooms) {
        validateDates(in, out);

        Guest guest = new Guest();
        guest.setFullName(name);
        guest.setPhone(phone);
        guest.setEmail(email);
        guest.setAddress(address);
        guest.setCity(city);
        guest.setProvince(province);
        guest.setPostal(postal);
        guest.setCountry(country);
        Guest savedGuest = guestService.save(guest);

        Reservation res = new Reservation();
        res.setGuest(savedGuest);
        res.setCheckIn(in);
        res.setCheckOut(out);
        res.setStatus(ReservationStatus.BOOKED);
        res.setAdults(adults);
        res.setChildren(children);

        List<ReservationRoom> roomAssociations = selectedRooms.stream().map(room -> {
            if (room == null || room.getType() == null)
                throw new IllegalStateException("Room or RoomType is null.");
            ReservationRoom rr = new ReservationRoom();
            rr.setReservation(res);
            rr.setRoom(room);
            rr.setRoomType(room.getType());
            rr.setQuantity(1);
            rr.setNightlyRate(pricingService.getBasePrice(room.getType()));
            return rr;
        }).collect(Collectors.toList());

        res.getRooms().addAll(roomAssociations);
        calculateAndSetTotals(res);
        reservationService.save(res);
    }


    public PricingService getPricingService() {
        return this.pricingService;
    }

    private void calculateAndSetTotals(Reservation res) {
        double subtotal = 0;
        for (ReservationRoom rr : res.getRooms()) {
            rr.setReservation(res); // ensure bi-directional link

            if (rr.getNightlyRate() == 0.0) {
                rr.setNightlyRate(pricingService.getBasePrice(rr.getRoomType()));
            }

            subtotal += pricingService.calculateStayPrice(
                    rr.getNightlyRate(), res.getCheckIn(), res.getCheckOut()
            ) * rr.getQuantity();
        }

        res.setSubtotal(subtotal);
        res.setTax(subtotal * 0.13);
        res.setTotal(subtotal + res.getTax());
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
    }
}
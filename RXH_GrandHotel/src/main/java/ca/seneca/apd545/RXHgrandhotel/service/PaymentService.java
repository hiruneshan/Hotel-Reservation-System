package ca.seneca.apd545.RXHgrandhotel.service;

import ca.seneca.apd545.RXHgrandhotel.model.Payment;
import ca.seneca.apd545.RXHgrandhotel.model.Reservation;
import ca.seneca.apd545.RXHgrandhotel.model.enums.PaymentType;
import ca.seneca.apd545.RXHgrandhotel.repository.PaymentRepository;
import ca.seneca.apd545.RXHgrandhotel.repository.ReservationRepository;
import java.time.LocalDate;

public class PaymentService {
    private final PaymentRepository paymentRepo;
    private final ReservationRepository reservationRepo;

    public PaymentService(PaymentRepository pr, ReservationRepository rr) {
        this.paymentRepo = pr;
        this.reservationRepo = rr;
    }

    public void processPayment(Reservation reservation, double amount, PaymentType type) {

        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setAmount(amount);
        payment.setType(type);
        payment.setDate(LocalDate.now());

        double newPaidTotal = reservation.getAmountPaid() + amount;
        reservation.setAmountPaid(newPaidTotal);
        reservation.updatePaidStatus();
        paymentRepo.save(payment);
        reservationRepo.save(reservation);
    }
}
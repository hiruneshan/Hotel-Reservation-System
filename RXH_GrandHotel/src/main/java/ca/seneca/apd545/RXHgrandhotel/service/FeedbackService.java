package ca.seneca.apd545.RXHgrandhotel.service;

import ca.seneca.apd545.RXHgrandhotel.model.Feedback;
import ca.seneca.apd545.RXHgrandhotel.model.Guest;
import ca.seneca.apd545.RXHgrandhotel.model.Reservation;
import ca.seneca.apd545.RXHgrandhotel.repository.FeedbackRepository;
import ca.seneca.apd545.RXHgrandhotel.repository.GuestRepository;
import ca.seneca.apd545.RXHgrandhotel.repository.ReservationRepository;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FeedbackService {

    private final FeedbackRepository feedbackRepo;
    private final ReservationRepository reservationRepo;
    private final GuestRepository guestRepo;


    public FeedbackService(FeedbackRepository feedbackRepo,
                           ReservationRepository reservationRepo,
                           GuestRepository guestRepo) {
        this.feedbackRepo = feedbackRepo;
        this.reservationRepo = reservationRepo;
        this.guestRepo = guestRepo;
    }


    public List<Feedback> findAll() {
        return feedbackRepo.findAll();
    }

    public List<Feedback> findByRating(int rating) {

        return feedbackRepo.findByRating(rating);
    }

    public Feedback submitFeedback(Long reservationId, int rating, String comment, String sentiment) {
        Optional<Reservation> resOpt = reservationRepo.findById(reservationId);
        if (resOpt.isEmpty()) {
            throw new IllegalArgumentException("Reservation not found");
        }

        Reservation reservation = resOpt.get();

        if (!reservation.isCheckedOut()) {
            throw new IllegalStateException("Feedback can only be submitted after checkout");
        }

        Feedback feedback = new Feedback();
        feedback.setReservation(reservation);
        feedback.setGuest(reservation.getGuest());
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedback.setSentiment(sentiment);
        feedback.setDate(LocalDate.now());

        return feedbackRepo.save(feedback);
    }
}
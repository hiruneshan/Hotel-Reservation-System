package ca.seneca.apd545.RXHgrandhotel.service;

import ca.seneca.apd545.RXHgrandhotel.model.Guest;
import ca.seneca.apd545.RXHgrandhotel.repository.GuestRepository;
import jakarta.persistence.EntityManagerFactory;

public class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(EntityManagerFactory emf) {
        this.guestRepository = new GuestRepository(emf);
    }

    public Guest save(Guest guest) {
        return guestRepository.save(guest);
    }

    public Guest findById(Long id) {
        return guestRepository.findById(id).orElse(null);
    }

    public Guest findByPhone(String phone) {
        return guestRepository.findByPhone(phone).orElse(null);
    }

    public Guest saveOrUpdate(Guest guest) {

        return guestRepository.findByPhone(guest.getPhone())
                .map(existing -> {
                    // Update existing guest info if needed
                    existing.setFullName(guest.getFullName());
                    existing.setEmail(guest.getEmail());
                    return guestRepository.save(existing);
                })
                .orElseGet(() -> guestRepository.save(guest)); // Create new if not found
    }
}

package ca.seneca.apd545.RXHgrandhotel.model;

import ca.seneca.apd545.RXHgrandhotel.model.enums.ReservationStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ReservationRoom> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    @Column(nullable = false)
    private LocalDate checkIn;

    @Column(nullable = false)
    private LocalDate checkOut;

    private int adults;
    private int children;

    private double subtotal;
    private double tax;
    private double total;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private double discountAmount;
    private double discountPercent;
    private String discountReason;
    private String appliedBy;

    @Column(nullable = false)
    private double amountPaid = 0.0;

    @Column(nullable = false)
    private boolean paid = false;

    @Column(nullable = false)
    private boolean checkedOut = false;


    @ElementCollection
    @CollectionTable(name = "reservation_addons", joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name = "addon_name")
    private List<String> appliedAddons = new ArrayList<>();

    public double getTotalAmount() {
        return this.total;
    }


    public Reservation() {}


    public void updatePaidStatus() {
        this.paid = (this.amountPaid >= (this.total - 0.01));
    }

    public double getOutstandingBalance() {
        return total - amountPaid;
    }

    public boolean canCheckOut() {
        return getOutstandingBalance() <= 0;
    }
    public void addRoom(Room room) {
        if (room != null) {
            ReservationRoom resRoom = new ReservationRoom();
            resRoom.setReservation(this);
            resRoom.setRoom(room);
            resRoom.setRoomType(room.getType());

            resRoom.setNightlyRate(room.getBasePrice());
            this.rooms.add(resRoom);
        }
    }


    public void setTotalAmount(double totalAmount) {
        this.total = totalAmount;
    }


    public Long getId() { return id; }

    public Guest getGuest() { return guest; }
    public void setGuest(Guest guest) { this.guest = guest; }

    public List<ReservationRoom> getRooms() { return rooms; }
    public void setRooms(List<ReservationRoom> rooms) { this.rooms = rooms; }

    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public int getAdults() { return adults; }
    public void setAdults(int adults) { this.adults = adults; }

    public int getChildren() { return children; }
    public void setChildren(int children) { this.children = children; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public boolean isCheckedOut() { return checkedOut; }
    public void setCheckedOut(boolean checkedOut) { this.checkedOut = checkedOut; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }

    public String getDiscountReason() { return discountReason; }
    public void setDiscountReason(String discountReason) { this.discountReason = discountReason; }

    public String getAppliedBy() { return appliedBy; }
    public void setAppliedBy(String appliedBy) { this.appliedBy = appliedBy; }

    public List<String> getAppliedAddons() { return appliedAddons; }
    public void setAppliedAddons(List<String> appliedAddons) { this.appliedAddons = appliedAddons; }
}
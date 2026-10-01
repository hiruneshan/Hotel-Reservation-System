package ca.seneca.apd545.RXHgrandhotel.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "loyalty_history")
public class LoyaltyHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loyalty_account_id", nullable = false)
    private LoyaltyAccount account;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private int points;

    @Column(nullable = false)
    private String description;

    public LoyaltyHistory() {}

    public Long getId() { return id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public void setAccount(LoyaltyAccount account) { this.account = account; }
}
package ca.seneca.apd545.RXHgrandhotel.model;

import ca.seneca.apd545.RXHgrandhotel.model.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "admin_users")
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public AdminUser() {}

    public AdminUser(String username, String passwordHash, Role role, String fullName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.fullName = fullName;
    }


    public Long getId(){ return id; }
    public String getUsername(){ return username; }
    public void setUsername(String username){ this.username = username; }
    public String getPasswordHash(){ return passwordHash; }
    public void setPasswordHash(String passwordHash){ this.passwordHash = passwordHash; }
    public Role getRole(){ return role; }
    public void setRole(Role role){ this.role = role; }
    public String getFullName(){ return fullName; }
    public void setFullName(String fullName){ this.fullName = fullName; }
    public boolean isActive(){ return isActive; }
    public void setActive(boolean active){ this.isActive = active; }


    /**
     * ADMIN   → max 15% discount
     * MANAGER → max 30% discount
     */
    public double getMaxDiscountPercent() {
        return switch (role) {
            case ADMIN -> 15.0;
            case MANAGER -> 30.0;
        };
    }

    @Override
    public String toString() {
        return fullName + " [" + role + "]";
    }
}
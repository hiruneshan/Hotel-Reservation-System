package ca.seneca.apd545.RXHgrandhotel.security;

import ca.seneca.apd545.RXHgrandhotel.model.AdminUser;
import ca.seneca.apd545.RXHgrandhotel.model.enums.Role;
import ca.seneca.apd545.RXHgrandhotel.repository.AdminUserRepository;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;

import java.util.Optional;

public class AuthService {

    private final AdminUserRepository adminUserRepository;
    private final BCrtptHasher hasher;

    private AdminUser currentUser;

    public AuthService(AdminUserRepository adminUserRepository, BCrtptHasher hasher) {
        this.adminUserRepository = adminUserRepository;
        this.hasher = hasher;
    }

    public LoginResult login(String username, String password, Role selectedRole) {


        if (username == null || username.isBlank()) {
            return LoginResult.failure("Username is required.");
        }
        if (password == null || password.isBlank()) {
            return LoginResult.failure("Password is required.");
        }
        if (selectedRole == null) {
            return LoginResult.failure("Please select a role.");
        }

        Optional<AdminUser> found = adminUserRepository.findByUsername(username);

        if (found.isEmpty()) {
            LoggerUtil.warn("LOGIN_FAILED - Unknown username: " + username);
            return LoginResult.failure("Invalid username or password.");
        }

        AdminUser user = found.get();

        if (user.getRole() != selectedRole) {
            LoggerUtil.warn("LOGIN_FAILED - Role mismatch for user: " + username);
            return LoginResult.failure("Invalid username or password.");
        }

        if (!hasher.verify(password, user.getPasswordHash())) {
            LoggerUtil.warn("LOGIN_FAILED - Wrong password for user: " + username);
            return LoginResult.failure("Invalid username or password.");
        }

        this.currentUser = user;
        LoggerUtil.info("LOGIN_SUCCESS - User: " + username + " Role: " + user.getRole());

        return LoginResult.success(user);
    }


    public void logout() {
        if (currentUser != null) {
            LoggerUtil.info("LOGOUT - User: " + currentUser.getUsername());
            currentUser = null;
        }
    }


    public AdminUser getCurrentUser()  { return currentUser; }
    public boolean isLoggedIn() { return currentUser != null; }


    /**
     * Validates a discount percentage against the current user's role cap.
     * ADMIN   → max 15%
     * MANAGER → max 30%
     *
     * @throws SecurityException if discount exceeds cap
     */
    public void validateDiscount(double discountPercent) {
        if (currentUser == null) throw new SecurityException("Not logged in.");
        if (discountPercent < 0) throw new IllegalArgumentException("Discount cannot be negative.");

        double cap = currentUser.getMaxDiscountPercent();
        if (discountPercent > cap) {
            LoggerUtil.warn("DISCOUNT_EXCEEDED - User: " + currentUser.getUsername()
                    + " tried " + discountPercent + "% (cap: " + cap + "%)");
            throw new SecurityException(
                    String.format("Your role (%s) allows max %.0f%% discount. Requested: %.1f%%",
                            currentUser.getRole(), cap, discountPercent));
        }
    }

    public boolean isManager() {
        return currentUser != null && currentUser.getRole() == Role.MANAGER;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }


    public static class LoginResult {
        private final boolean   success;
        private final String    message;
        private final AdminUser user;

        private LoginResult(boolean success, String message, AdminUser user) {
            this.success = success;
            this.message = message;
            this.user    = user;
        }

        public static LoginResult success(AdminUser user) {
            return new LoginResult(true, "Login successful.", user);
        }

        public static LoginResult failure(String message) {
            return new LoginResult(false, message, null);
        }

        public boolean isSuccess()  { return success; }
        public String getMessage()  { return message; }
        public AdminUser getUser()  { return user; }
    }
}
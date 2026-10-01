package ca.seneca.apd545.RXHgrandhotel.session;

import ca.seneca.apd545.RXHgrandhotel.model.enums.RoomType;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;



public class BookingSession {


    private static Long guestId;
    private static String loyaltyNumber;
    private static boolean loyaltyMember = false;
    private static String loyaltyStatus;

    public static Long getGuestId() { return guestId; }
    public static void setGuestId(Long id) { guestId = id; }

    public static String getLoyaltyNumber() { return loyaltyNumber; }
    public static void setLoyaltyNumber(String number) { loyaltyNumber = number; }

    public static boolean isLoyaltyMember() { return loyaltyMember; }
    public static void setLoyaltyMember(boolean value) { loyaltyMember = value; }

    public static String getLoyaltyStatus() { return loyaltyStatus; }
    public static void setLoyaltyStatus(String status) { loyaltyStatus = status; }

    private static LocalDate checkIn;
    private static LocalDate checkOut;
    private static int guestCount;

    public static LocalDate getCheckIn() { return checkIn; }
    public static void setCheckIn(LocalDate date) { checkIn = date; }

    public static LocalDate getCheckOut() { return checkOut; }
    public static void setCheckOut(LocalDate date) { checkOut = date; }

    public static int getGuestCount() { return guestCount; }
    public static void setGuestCount(int count) { guestCount = count; }

    private static Map<RoomType, Integer> roomQuantities = new HashMap<>();
    private static Map<String, Boolean> addons = new HashMap<>();

    public static void setRoomQuantity(RoomType type, int quantity) { roomQuantities.put(type, quantity); }
    public static int getRoomQuantity(RoomType type) { return roomQuantities.getOrDefault(type, 0); }
    public static Map<RoomType, Integer> getAllRoomQuantities() { return roomQuantities; }

    public static void setAddon(String name, boolean selected) { addons.put(name, selected); }
    public static boolean getAddon(String name) { return addons.getOrDefault(name, false); }
    public static Map<String, Boolean> getAllAddons() { return addons; }

    private static String guestFullName;
    private static String guestPhone;
    private static String guestEmail;
    private static LocalDate guestDob;
    private static String guestAddress;
    private static String guestCity;
    private static String guestProvince;
    private static String guestPostal;
    private static String guestCountry;

    public static void setGuestFullName(String v) { guestFullName = v; }
    public static String getGuestFullName() { return guestFullName; }
    public static void setGuestPhone(String v) { guestPhone = v; }
    public static String getGuestPhone() { return guestPhone; }
    public static void setGuestEmail(String v) { guestEmail = v; }
    public static String getGuestEmail() { return guestEmail; }
    public static void setGuestDob(LocalDate v) { guestDob = v; }
    public static LocalDate getGuestDob() { return guestDob; }
    public static void setGuestAddress(String v) { guestAddress = v; }
    public static String getGuestAddress() { return guestAddress; }
    public static void setGuestCity(String v) { guestCity = v; }
    public static String getGuestCity() { return guestCity; }
    public static void setGuestProvince(String v) { guestProvince = v; }
    public static String getGuestProvince() { return guestProvince; }
    public static void setGuestPostal(String v) { guestPostal = v; }
    public static String getGuestPostal() { return guestPostal; }
    public static void setGuestCountry(String v) { guestCountry = v; }
    public static String getGuestCountry() { return guestCountry; }

    private static double subtotal;
    private static double tax;
    private static double total;

    public static double getSubtotal() { return subtotal; }
    public static void setSubtotal(double v) { subtotal = v; }
    public static double getTax() { return tax; }
    public static void setTax(double v) { tax = v; }
    public static double getTotal() { return total; }
    public static void setTotal(double v) { total = v; }

    private static String bookingId;
    private static Long reservationId;

    public static void setBookingId(String id) { bookingId = id; }
    public static String getBookingId() { return bookingId; }
    public static Long getReservationId() { return reservationId; }
    public static void setReservationId(Long id) { reservationId = id; }

    /**
     * Resets the session after a successful booking or cancellation.
     */
    public static void reset() {
        guestId = null;
        loyaltyNumber = null;
        loyaltyMember = false;
        loyaltyStatus = null;
        checkIn = null;
        checkOut = null;
        guestCount = 0;
        roomQuantities.clear();
        addons.clear();
        guestFullName = null;
        guestPhone = null;
        guestEmail = null;
        guestDob = null;
        guestAddress = null;
        guestCity = null;
        guestProvince = null;
        guestPostal = null;
        guestCountry = null;
        subtotal = 0.0;
        tax = 0.0;
        total = 0.0;
        bookingId = null;
        reservationId = null;
    }
}
package ca.seneca.apd545.RXHgrandhotel.events;

import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;

/**
 * Observer must be used to notify administrators
 * when room availability changes
 */
public class AdminObserver {
    private String adminName;

    public AdminObserver(String adminName) {
        this.adminName = adminName;
    }

    public void onRoomAvailable(String roomNumber, String roomType) {

        String message = String.format("NOTIFICATION for %s: Room %s (%s) is now AVAILABLE.",
                adminName, roomNumber, roomType);
        System.out.println(message);
        LoggerUtil.info("OBSERVER_NOTIFIED - " + message);
    }
}
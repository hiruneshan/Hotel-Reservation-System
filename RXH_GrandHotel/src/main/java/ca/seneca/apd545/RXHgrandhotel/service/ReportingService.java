package ca.seneca.apd545.RXHgrandhotel.service;

import ca.seneca.apd545.RXHgrandhotel.model.ActivityLog;
import ca.seneca.apd545.RXHgrandhotel.model.Reservation;
import ca.seneca.apd545.RXHgrandhotel.model.dto.OccupancyReportDTO;
import ca.seneca.apd545.RXHgrandhotel.model.dto.RevenueReportDTO;
import ca.seneca.apd545.RXHgrandhotel.model.enums.RoomType;
import ca.seneca.apd545.RXHgrandhotel.repository.ActivityLogRepository;
import ca.seneca.apd545.RXHgrandhotel.repository.ReservationRepository;
import ca.seneca.apd545.RXHgrandhotel.repository.RoomRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportingService {
    private final ReservationRepository reservationRepo;
    private final RoomRepository roomRepo;
    private final ActivityLogRepository logRepo;

    public ReportingService(ReservationRepository reservationRepo, RoomRepository roomRepo, ActivityLogRepository logRepo) {
        this.reservationRepo = reservationRepo;
        this.roomRepo = roomRepo;
        this.logRepo = logRepo;
    }

    public List<ActivityLog> getActivityLogs() {
        return logRepo.findAll();
    }

    public void addLog(String actor, String action, String type, String id, String msg) {
        logRepo.save(new ActivityLog(actor, action, type, id, msg));
    }
    public List<RevenueReportDTO> getRevenueData(LocalDate start, LocalDate end, RoomType type) {

        List<Reservation> reservations = reservationRepo.findAll();

        List<Reservation> filtered = reservations.stream()
                .filter(r -> r.getCheckIn() != null && r.getCheckOut() != null)
                .filter(r -> !r.getCheckIn().isBefore(start) && !r.getCheckOut().isAfter(end))
                .filter(r -> type == null || r.getRooms().stream().anyMatch(room -> room.getRoom().getType() == type))
                .toList();


        double subtotal = filtered.stream().mapToDouble(Reservation::getSubtotal).sum();
        double tax = filtered.stream().mapToDouble(Reservation::getTax).sum();
        double discounts = filtered.stream().mapToDouble(Reservation::getDiscountAmount).sum();
        double total = filtered.stream().mapToDouble(Reservation::getTotal).sum();

        List<RevenueReportDTO> report = new ArrayList<>();
        report.add(new RevenueReportDTO(start + " to " + end, filtered.size(), subtotal, tax, discounts, total));

        addLog("Admin", "REPORT", "Revenue", "N/A", "Generated revenue report for " + start + " to " + end);

        return report;
    }


    public List<OccupancyReportDTO> getOccupancyData(LocalDate start, LocalDate end, RoomType type) {

        List<Reservation> reservations = reservationRepo.findAll();
        long totalRoomsCount = roomRepo.findAll().size();


        List<Reservation> activeReservations = reservations.stream()
                .filter(r -> !r.getCheckOut().isBefore(start) && !r.getCheckIn().isAfter(end))
                .filter(r -> type == null || r.getRooms().stream().anyMatch(room -> room.getRoom().getType() == type))
                .toList();

        int occupied = activeReservations.size();
        int available = (int) totalRoomsCount - occupied;
        double percent = totalRoomsCount > 0 ? ((double) occupied / totalRoomsCount) * 100 : 0;
        List<OccupancyReportDTO> report = new ArrayList<>();
        report.add(new OccupancyReportDTO(start.toString(), available, occupied, percent));


        addLog("Admin", "REPORT", "Occupancy", "N/A", "Generated occupancy report");

        return report;
    }
}
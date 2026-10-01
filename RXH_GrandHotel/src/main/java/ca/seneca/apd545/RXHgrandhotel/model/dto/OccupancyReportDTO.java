package ca.seneca.apd545.RXHgrandhotel.model.dto;

public class OccupancyReportDTO {
    private String date;
    private int available;
    private int occupied;
    private double percent;

    public OccupancyReportDTO(String date, int available, int occupied, double percent) {
        this.date = date;
        this.available = available;
        this.occupied = occupied;
        this.percent = percent;
    }

    public String getDate() { return date; }
    public int getAvailable() { return available; }
    public int getOccupied() { return occupied; }
    public double getPercent() { return percent; }
}
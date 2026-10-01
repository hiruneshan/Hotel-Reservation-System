package ca.seneca.apd545.RXHgrandhotel.model.dto;

public class RevenueReportDTO {
    private String period;
    private int count;
    private double subtotal;
    private double tax;
    private double discounts;
    private double total;

    public RevenueReportDTO(String period, int count, double subtotal, double tax, double discounts, double total) {
        this.period = period;
        this.count = count;
        this.subtotal = subtotal;
        this.tax = tax;
        this.discounts = discounts;
        this.total = total;
    }


    public String getPeriod() { return period; }
    public int getCount() { return count; }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public double getDiscounts() { return discounts; }
    public double getTotal() { return total; }
}
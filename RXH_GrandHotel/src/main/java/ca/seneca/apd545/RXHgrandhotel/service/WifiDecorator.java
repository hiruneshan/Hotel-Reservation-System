package ca.seneca.apd545.RXHgrandhotel.service;

public class WifiDecorator extends AddonDecorator {
    public WifiDecorator(Billable newBill) { super(newBill); }

    @Override
    public double getPrice() { return tempBill.getPrice() + 15.0; }

    @Override
    public String getDescription() { return tempBill.getDescription() + ", High-Speed Wi-Fi"; }
}
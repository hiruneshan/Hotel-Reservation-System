package ca.seneca.apd545.RXHgrandhotel.service;

public class SpaDecorator extends AddonDecorator {
    public SpaDecorator(Billable newBill) { super(newBill); }

    @Override
    public double getPrice() { return tempBill.getPrice() + 50.0; }

    @Override
    public String getDescription() { return tempBill.getDescription() + ", Spa Service"; }
}
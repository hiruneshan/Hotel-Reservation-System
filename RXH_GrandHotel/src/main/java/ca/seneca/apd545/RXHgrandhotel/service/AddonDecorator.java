package ca.seneca.apd545.RXHgrandhotel.service;

import ca.seneca.apd545.RXHgrandhotel.service.Billable;

public abstract class AddonDecorator implements Billable {
    protected Billable tempBill;

    public AddonDecorator(Billable newBill) {
        this.tempBill = newBill;
    }

    @Override
    public double getPrice() { return tempBill.getPrice(); }

    @Override
    public String getDescription() { return tempBill.getDescription(); }
}
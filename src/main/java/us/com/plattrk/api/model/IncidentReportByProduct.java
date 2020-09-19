package us.com.plattrk.api.model;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import us.com.plattrk.util.JsonDateMinusTimeDeserializer;

public class IncidentReportByProduct {

    private String products;
    private String address;
    private Date startDate;
    private Date endDate;

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @JsonDeserialize(using = JsonDateMinusTimeDeserializer.class)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @JsonDeserialize(using = JsonDateMinusTimeDeserializer.class)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

}

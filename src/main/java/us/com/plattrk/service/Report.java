package us.com.plattrk.service;

import us.com.plattrk.api.model.EmailAddress;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentReportByProduct;

import java.util.Date;
import java.util.List;

public interface Report {

    public void generateDailyReport(List<Incident> incidents, Date previousDayDate);

    public void generateWeekEndReport(List<Incident> incidents, Date previousWeekEndDate, Date previousDayDate);

    public boolean generateWeeklyReport(List<Incident> incidents, Date previousWeeklyDate, Date previousDayDate, EmailAddress address);

    public boolean generateReportByProduct(List<Incident> incidents, IncidentReportByProduct incidentReport);
    
}

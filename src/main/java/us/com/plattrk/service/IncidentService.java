package us.com.plattrk.service;

import us.com.plattrk.api.model.EmailAddress;
import us.com.plattrk.api.model.ErrorCondition;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentChronology;
import us.com.plattrk.api.model.IncidentGroup;
import us.com.plattrk.api.model.IncidentReportByProduct;
import us.com.plattrk.api.model.Product;
import us.com.plattrk.api.model.ReferenceData;
import us.com.plattrk.api.model.ToggleSwitch;

import java.util.List;
import java.util.Set;

public interface IncidentService {

    public Set<Incident> getIncidents();

    public Incident deleteIncident(Long id);

    public Incident saveIncident(Incident incident);

    public Incident getIncident(Long id);

    public IncidentGroup getGroup(Long id);

    public Set<IncidentGroup> getGroups();

    public Set<IncidentChronology> getChronologies(Long id);

    public Set<Product> getProducts(Long id);

    public void notificationCheck();

    public void dailyReport();

    public void weekEndReport();

    public void weeklyReport();

    public boolean generateWeeklyReport(EmailAddress address);

    public boolean generateIncidentReportByProduct(IncidentReportByProduct report);

    public List<Incident> getOpenIncidents();

    public boolean toggleAutoWeeklyReport(ToggleSwitch action);

    public boolean isToggleAutoWeeklyReport();

    public ErrorCondition getErrorCode(Long id);

    public ReferenceData getApplicationStatus(Long id);

}

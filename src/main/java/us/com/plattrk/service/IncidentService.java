package us.com.plattrk.service;

import us.com.plattrk.api.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface IncidentService {

    public Set<Incident> getIncidents();

    public PageWrapper<Incident> search(Map<String, String> filtersMap);

    public Incident deleteIncident(Long id);

    public Incident saveIncident(Incident incident);

    public void notificationCheck();

    public void dailyReport();

    public void weekEndReport();

    public void weeklyReport();

    public boolean generateWeeklyReport(EmailAddress address);

    public boolean generateIncidentReportByProduct(IncidentReportByProduct report);

    public boolean toggleAutoWeeklyReport(ToggleSwitch action);

    public boolean isToggleAutoWeeklyReport();

    public IncidentGroup getGroup(Long id);

    public Set<IncidentGroup> getGroups();

    public Set<IncidentChronology> getChronologies(Long id);

    public Set<Product> getProducts(Long id);

    public List<Incident> getOpenIncidents();

    public ErrorCondition getErrorCode(Long id);

    public ReferenceData getApplicationStatus(Long id);

    public Optional<Incident> getIncident(Long id);

}

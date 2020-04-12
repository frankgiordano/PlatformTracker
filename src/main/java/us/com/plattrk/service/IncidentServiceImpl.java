package us.com.plattrk.service;

import us.com.plattrk.api.model.EmailAddress;
import us.com.plattrk.api.model.ErrorCondition;
import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentChronology;
import us.com.plattrk.api.model.IncidentGroup;
import us.com.plattrk.api.model.IncidentReportByProduct;
import us.com.plattrk.api.model.NotificationThread;
import us.com.plattrk.api.model.Product;
import us.com.plattrk.api.model.ReferenceData;
import us.com.plattrk.api.model.ToggleSwitch;
import us.com.plattrk.repository.IncidentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

@Service(value = "IncidentService")
public class IncidentServiceImpl implements IncidentService, ServletContextAware {

    private static Logger log = LoggerFactory.getLogger(IncidentServiceImpl.class);

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private Properties appProperties;

    @Autowired
    private MailService mailService;

    @Autowired
    private Report report;

    @Autowired
    private ServletContext servletContext;

    @Override
    public Set<Incident> getIncidents() {
        return incidentRepository.getIncidents();
    }

    @Override
    public List<Incident> getOpenIncidents() {
        return incidentRepository.getOpenIncidents();
    }

    @Override
    @Transactional
    public Incident deleteIncident(Long id) {
        return incidentRepository.deleteIncident(id);
    }

    @Override
    @Transactional
    public Incident saveIncident(Incident incident) {

        if ((incident.getId() == null) && (incidentRepository.saveIncident(incident) != null)) {
            WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            MailService mailService = (MailService) wac.getBean("mailService");
            if (incident.getStatus().equals("Open")) {
                mailService.send(incident, appProperties, Mail.Type.INCIDENTSTART);
            } else {
                mailService.send(incident, appProperties, Mail.Type.INCIDENTCREATEEND);
            }
        } else incidentRepository.saveIncident(incident);

        return incident;
    }

    //	@Scheduled(cron="*/10 * * * * ?")
    public void notificationCheck() {

        List<Incident> openIncidents = incidentRepository.getOpenIncidents();
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

        // find if there are any open incidents, if so loop through them and perform email notification if applicable. 
        // spawn a thread for each open incident and the thread performs the notification.
        if (!openIncidents.isEmpty()) {

            for (Incident incident : openIncidents) {
                log.info("Open Incident found, in NotificationCheck processing " + incident.getStatus() + " " + incident.getTag() + " " + incident.getStartTime());

                if (!getThreadByName(incident.getTag())) {
//					Thread thread = new Thread(new NotificationThread (i, appProperties)); // do this if you do not want to use spring container
                    NotificationThread notificationThread = (NotificationThread) wac.getBean("notificationThread");
                    notificationThread.setIncident(incident);
                    notificationThread.setAppProperties(appProperties);
                    Thread thread = new Thread(notificationThread);
                    thread.setName(incident.getTag());
                    thread.start();
                }
            }

        }
    }

    //	@Scheduled(cron="0 0 7 * * TUE-FRI")
    public void dailyReport() {

        Calendar calPrevious = Calendar.getInstance();
        calPrevious.add(Calendar.DAY_OF_YEAR, -1);
        Date previousDayDate = new Date(calPrevious.getTimeInMillis());

        List<Incident> incidents = incidentRepository.getDateRangeIncidents(previousDayDate, new Date());
        report.generateDailyReport(incidents, previousDayDate);
    }

    //	@Scheduled(cron="0 0 7 * * MON")
    public void weekEndReport() {

        Calendar calWeekEnd = Calendar.getInstance();
        Calendar calPrevious = Calendar.getInstance();
        calWeekEnd.add(Calendar.DAY_OF_YEAR, -3);
        calPrevious.add(Calendar.DAY_OF_YEAR, -1);
        Date previousWeekEndDate = new Date(calWeekEnd.getTimeInMillis());
        Date previousDayDate = new Date(calPrevious.getTimeInMillis());

        List<Incident> incidents = incidentRepository.getDateRangeIncidents(previousWeekEndDate, new Date());
        report.generateWeekEndReport(incidents, previousWeekEndDate, previousDayDate);
    }

    //	@Scheduled(cron="0 0 10 * * MON")
    public void weeklyReport() {

        if (isToggleAutoWeeklyReport()) {
            return;
        }

        Calendar calWeekly = Calendar.getInstance();
        Calendar calPrevious = Calendar.getInstance();
        calWeekly.add(Calendar.DAY_OF_YEAR, -7);
        calPrevious.add(Calendar.DAY_OF_YEAR, -1);
        Date previousWeeklyDate = new Date(calWeekly.getTimeInMillis());
        Date previousDayDate = new Date(calPrevious.getTimeInMillis());

//		List<Incident> incidents = incidentRepository.getDateRangeIncidentsByPriority(previousWeeklyDate, new Date(), "P1");
        List<Incident> incidents = incidentRepository.getDateRangeIncidentsByApplicationStatus(previousWeeklyDate, new Date(), "Down");
        report.generateWeeklyReport(incidents, previousWeeklyDate, previousDayDate, null);
    }

    @Override
    public boolean generateWeeklyReport(EmailAddress address) {

        Calendar calWeekly = Calendar.getInstance();
        Calendar calPrevious = Calendar.getInstance();
        calWeekly.add(Calendar.DAY_OF_YEAR, -7);
        calPrevious.add(Calendar.DAY_OF_YEAR, -1);
        Date previousWeeklyDate = new Date(calWeekly.getTimeInMillis());
        Date previousDayDate = new Date(calPrevious.getTimeInMillis());

//		List<Incident> incidents = incidentRepository.getDateRangeIncidentsByPriority(previousWeeklyDate, new Date(), "P1");
        List<Incident> incidents = incidentRepository.getDateRangeIncidentsByApplicationStatus(previousWeeklyDate, new Date(), "Down");
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        Report report = (Report) wac.getBean("Report");
        if (address.getAddress().toLowerCase().equals("auto")) {
            return report.generateWeeklyReport(incidents, previousWeeklyDate, previousDayDate, null);
        }
        return report.generateWeeklyReport(incidents, previousWeeklyDate, previousDayDate, address);
    }

    @Override
    public boolean generateIncidentReportByProduct(IncidentReportByProduct incidentReport) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(incidentReport.getEndDate());
        calendar.add(Calendar.DATE, 1);
        Date addedDayDate = new Date(calendar.getTimeInMillis());

        List<Incident> incidents = incidentRepository.getDateRangeIncidents(incidentReport.getStartDate(), addedDayDate);
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        Report report = (Report) wac.getBean("Report");
        return report.generateReportByProduct(incidents, incidentReport);
    }

    @Override
    public boolean toggleAutoWeeklyReport(ToggleSwitch value) {

        try {
            File newFile = new File(fileName());

            if (value.getAction().equals("ON")) {
                if (!newFile.exists()) {
                    newFile.createNewFile();
                }
            } else if (value.getAction().equals("OFF")) {
                newFile.delete();
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean isToggleAutoWeeklyReport() {
        File toggleFile = new File(fileName());
        if (toggleFile.exists()) {
            return true;
        }
        return false;
    }

    public String fileName() {
        String file;
        if (System.getProperty("os.name").startsWith("Windows")) {
            file = "c:\\toggle";
        } else {
            file = appProperties.getProperty("ReportLocation") + "//toggle";
        }
        return file;
    }

    public boolean getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) return true;
        }
        return false;
    }

    @Override
    public Incident getIncident(Long id) {
        return incidentRepository.getIncident(id);
    }

    @Override
    public IncidentGroup getGroup(Long id) {
        return incidentRepository.getGroup(id);
    }

    @Override
    public Set<IncidentGroup> getGroups() {
        return incidentRepository.getGroups();
    }

    @Override
    public Set<Product> getProducts(Long id) {
        return incidentRepository.getProducts(id);
    }

    @Override
    public Set<IncidentChronology> getChronologies(Long id) {
        return incidentRepository.getChronologies(id);
    }

    @Override
    public ErrorCondition getErrorCode(Long id) {
        return incidentRepository.getErrorCode(id);
    }

    @Override
    public ReferenceData getApplicationStatus(Long id) {
        return incidentRepository.getApplicationStatus(id);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}

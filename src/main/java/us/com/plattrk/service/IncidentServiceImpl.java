package us.com.plattrk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import us.com.plattrk.api.model.*;
import us.com.plattrk.repository.IncidentRepository;
import us.com.plattrk.repository.NotificationRepository;

import javax.mail.SendFailedException;
import javax.persistence.OptimisticLockException;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service(value = "IncidentService")
public class IncidentServiceImpl implements IncidentService, ServletContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentServiceImpl.class);

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private Properties appProperties;

    @Autowired
    private Report report;

    @Autowired
    private ServletContext servletContext;

    @Override
    public Set<Incident> getIncidents() {
        return incidentRepository.getIncidents();
    }

    @Override
    @Transactional
    public PageWrapper<Incident> search(Map<String, String> filtersMap) {
        return incidentRepository.getIncidentsByCriteria(filtersMap);
    }

    @Override
    @Transactional
    public Incident deleteIncident(Long id) {
        return incidentRepository.deleteIncident(id);
    }

    @Override
    @Transactional
    public Incident saveIncident(Incident incident) throws OptimisticLockException {
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        MailService mailService = (MailService) wac.getBean("mailService");
        Incident result;
        if ((incident.getId() == null)) {
            result = incidentRepository.saveIncident(incident);
            if (result != null) {
                try {
                    if ("Open".equals(incident.getStatus())) {
                        mailService.send(incident, appProperties, Mail.Type.INCIDENTSTART);
                    } else {
                        mailService.send(incident, appProperties, Mail.Type.INCIDENTCREATEEND);
                    }
                } catch (SendFailedException e) {
                    LOG.error("IncidentServiceImpl::saveIncident - error sending email notification ", e);
                }

                addIncidentNotificationEntry(result);
            }
        } else {
            // The incoming incident for saving may be marked as closed, check if it is.
            // If it is marked as Closed, check the current Incident record in the DB
            // before saving. See if it is marked as Open. If so, then the Incident is
            // being set as Closed. In this case handle this as a special action to
            // clear up its Notification table entry and to send out Closed notification
            // from here instead of the notification service which was done with the
            // legacy notification process before..
            Optional<Incident> currentIncidentInDB = incidentRepository.getIncident(incident.getId());
            if ("Closed".equals(incident.getStatus())) {
                currentIncidentInDB.ifPresent((i -> {
                    if ("Open".equals(i.getStatus())) {  // currently set as Open in DB
                        sentIncidentEndNotification(incident, mailService);
                        deleteIncidentNotificationEntry(incident);
                    }
                }));
            } else {  // incoming incident is set to Open
                currentIncidentInDB.ifPresent((i -> {
                    if ("Closed".equals(i.getStatus())) {  // current set as Closed in DB
                        addIncidentNotificationEntry(incident);
                    }
                }));
            }
            result = incidentRepository.saveIncident(incident);
        }

        return result;
    }

    //    @Scheduled(cron="*/10 * * * * ?")  //
    public void notificationCheckLegacy() {  // this was original design lets call it legacy now...
        List<Incident> openIncidents = incidentRepository.getOpenIncidents();
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

        // find if there are any open incidents, if so loop through them and perform email notification if applicable.
        // spawn a thread for each open incident and the thread performs the notification.
        openIncidents.forEach((i -> {
            notificationCheckInfo(i);

            if (!getThreadByName(i.getTag())) {
                // do the following new Thread if you do not want to use spring container
                // Thread thread = new Thread(new NotificationThread (i, appProperties));
                IncidentNotificationLegacyServiceImpl incidentNotificationService =
                        (IncidentNotificationLegacyServiceImpl) wac.getBean("incidentNotificationThreadServiceImpl");
                incidentNotificationService.setIncident(i);
                incidentNotificationService.setAppProperties(appProperties);
                Thread thread = new Thread(incidentNotificationService);
                thread.setName(i.getTag());
                thread.start();
            }
        }));
    }

    @Override
    @Scheduled(cron = "*/5 * * * * ?")
    @Transactional
    public void notificationCheck() {
        List<Incident> openIncidents = incidentRepository.getOpenIncidents();

        // find if there are any open incidents, if so loop through them and perform email notification if applicable.
        openIncidents.forEach((i -> {
            notificationCheckInfo(i);

            Notification notification = notificationRepository.getNotification(EntityType.INCIDENT.name(), i.getId());
            if (notification != null) {
                alertLifeCycle(i);
            }
        }));
    }

    @Override
    @Scheduled(cron = "0 0 7 * * TUE-FRI")
    public void dailyReport() {
        Calendar calPrevious = Calendar.getInstance();
        calPrevious.add(Calendar.DAY_OF_YEAR, -1);
        Date previousDayDate = new Date(calPrevious.getTimeInMillis());

        List<Incident> incidents = incidentRepository.getDateRangeIncidents(previousDayDate, new Date());
        report.generateDailyReport(incidents, previousDayDate);
    }

    @Override
    @Scheduled(cron = "0 0 7 * * MON")
    public void weekEndReport() {
        SetWeekPrevCalendars calendars = new SetWeekPrevCalendars(-3, -1).invoke();

        List<Incident> incidents = incidentRepository.getDateRangeIncidents(calendars.getPreviousWeekDate(), new Date());
        report.generateWeekEndReport(incidents, calendars.getPreviousWeekDate(), calendars.getPreviousDayDate());
    }

    @Override
    @Scheduled(cron = "0 0 10 * * MON")
    public void weeklyReport() {
        if (isToggleAutoWeeklyReport()) {
            return;
        }

        SetWeekPrevCalendars calendars = new SetWeekPrevCalendars(-7, -1).invoke();
        List<Incident> incidents = incidentRepository.getDateRangeIncidentsByApplicationStatus(
                calendars.getPreviousWeekDate(), new Date(), "Down");
        report.generateWeeklyReport(incidents, calendars.getPreviousWeekDate(), calendars.getPreviousDayDate(), null);
    }

    @Override
    public boolean generateWeeklyReport(EmailAddress address) {
        SetWeekPrevCalendars calendars = new SetWeekPrevCalendars(-7, -1).invoke();

        List<Incident> incidents = incidentRepository.getDateRangeIncidentsByApplicationStatus(
                calendars.getPreviousWeekDate(), new Date(), "Down");
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        Report report = (Report) wac.getBean("Report");

        if ("auto".equalsIgnoreCase(address.getAddress())) {
            return report.generateWeeklyReport(incidents, calendars.getPreviousWeekDate(),
                    calendars.getPreviousDayDate(), null);
        }

        return report.generateWeeklyReport(incidents, calendars.getPreviousWeekDate(),
                calendars.getPreviousDayDate(), address);
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

            if ("ON".equals(value.getAction())) {
                if (!newFile.exists()) {
                    newFile.createNewFile();
                }
            } else if ("OFF".equals(value.getAction())) {
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

    @Override
    public boolean isToggleAutoWeeklyReport() {
        File toggleFile = new File(fileName());
        return toggleFile.exists();
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
    public Set<IncidentChronology> getChronologies(Long id) {
        return incidentRepository.getChronologies(id);
    }

    @Override
    public Set<Product> getProducts(Long id) {
        return incidentRepository.getProducts(id);
    }

    @Override
    public List<Incident> getOpenIncidents() {
        return incidentRepository.getOpenIncidents();
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
    public Optional<Incident> getIncident(Long id) {
        return incidentRepository.getIncident(id);
    }

    private void alertLifeCycle(Incident incident) {
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        IncidentNotificationService incidentNotificationService =
                (IncidentNotificationServiceImpl) wac.getBean("incidentNotificationServiceImpl");
        incidentNotificationService.setIncident(incident);

        try {
            incidentNotificationService.resetAlert();
            incidentNotificationService.earlyAlert();
            incidentNotificationService.alertOffSet();
            incidentNotificationService.escalatedAlert();
        } catch (IllegalStateException e) {
            LOG.error("IncidentServiceImpl::alertLifeCycle - IllegalStateException error - {} ", e.getMessage());
        }
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    private void addIncidentNotificationEntry(Incident result) {
        LocalDateTime startDateTime = result.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Notification entry = new Notification(result.getId(), startDateTime, EntityType.INCIDENT.name(),
                result.getChronologies().size());
        notificationRepository.save(entry);
    }

    private void sentIncidentEndNotification(Incident incident, MailService mailService) {
        try {
            mailService.send(incident, appProperties, Mail.Type.INCIDENTEND);
        } catch (SendFailedException e) {
            LOG.error("IncidentServiceImpl::sentIncidentEndNotification - error sending email notification ", e);
        }
    }

    private void deleteIncidentNotificationEntry(Incident incident) {
        Notification notification = notificationRepository.getNotification(
                EntityType.INCIDENT.name(), incident.getId());
        if (notification != null)
            notificationRepository.delete(notification.getId());
    }

    private void notificationCheckInfo(Incident i) {
        StringBuilder str = new StringBuilder();
        str.append("IncidentServiceImpl::notificationCheck - ");
        str.append("open Incident found in NotificationCheck, processing incident status ");
        str.append(i.getStatus());
        str.append(", tag ");
        str.append(i.getTag());
        str.append(", startTime ");
        str.append(i.getStartTime());
        LOG.info(str.toString());
    }

    private String fileName() {
        String file;
        if (System.getProperty("os.name").startsWith("Windows")) {
            file = "c:\\plat_trk_rpt_on";
        } else {
            file = appProperties.getProperty("ReportLocation") + "//plat_trk_rpt_on";
        }
        return file;
    }

    private boolean getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (threadName.equals(t.getName()))
                return true;
        }
        return false;
    }

    private static class SetWeekPrevCalendars {
        private final int week;
        private final int previous;
        private Date previousWeekDate;
        private Date previousDayDate;

        public SetWeekPrevCalendars(int week, int previous) {
            this.week = week;
            this.previous = previous;
        }

        public Date getPreviousWeekDate() {
            return previousWeekDate;
        }

        public Date getPreviousDayDate() {
            return previousDayDate;
        }

        public SetWeekPrevCalendars invoke() {
            Calendar calWeekly = Calendar.getInstance();
            Calendar calPrevious = Calendar.getInstance();
            calWeekly.add(Calendar.DAY_OF_YEAR, week);
            calPrevious.add(Calendar.DAY_OF_YEAR, previous);
            previousWeekDate = new Date(calWeekly.getTimeInMillis());
            previousDayDate = new Date(calPrevious.getTimeInMillis());
            return this;
        }
    }

}

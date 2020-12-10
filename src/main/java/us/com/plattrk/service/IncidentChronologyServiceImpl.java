package us.com.plattrk.service;

import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import us.com.plattrk.api.model.Incident;
import us.com.plattrk.api.model.IncidentChronology;
import us.com.plattrk.repository.IncidentChronologyRepository;
import us.com.plattrk.service.Mail.Type;

@Service(value = "IncidentChronologyService")
public class IncidentChronologyServiceImpl implements IncidentChronologyService {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentChronologyServiceImpl.class);

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private IncidentChronologyRepository incidentChronologyRepository;

    @Autowired
    private Properties appProperties;

    @Override
    public Set<IncidentChronology> getChronologies() {
        return incidentChronologyRepository.getChronologies();
    }

    @Override
    @Transactional
    public IncidentChronology deleteIncidentChronology(Long id) {
        return incidentChronologyRepository.deleteIncidentChronology(id);
    }

    @Override
    public IncidentChronology saveIncidentChronology(IncidentChronology chronology) {
        boolean sendNotification = true;
        
        if (chronology.getId() != null) {
            IncidentChronology item = incidentChronologyRepository.getIncidentChronology(chronology.getId());
            if (chronology.getDescription().equals(item.getDescription()))
                sendNotification = false;
        }

        if (incidentChronologyRepository.saveIncidentChronology(chronology) != null) {
            Incident incident = incidentChronologyRepository.getIncidentOfNewChronology(chronology.getIncident().getId());
            WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            MailService mailService = (MailService) wac.getBean("mailService");
            try {
                if (sendNotification)
                    mailService.send(incident, appProperties, Type.INCIDENTCHRONOLOGYSTART);
            } catch (Exception e) {
                LOG.error("IncidentChronologyServiceImpl::saveIncidentChronology - error sending email notification ", e);
            }
        }

        return chronology;
    }

    @Override
    public IncidentChronology getIncidentChronology(Long id) {
        return incidentChronologyRepository.getIncidentChronology(id);
    }

}

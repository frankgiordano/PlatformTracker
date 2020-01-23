package us.com.plattrk.service;

import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

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
	
//	private static Logger log = LoggerFactory.getLogger(IncidentRepositoryImpl.class);
	
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
	public boolean deleteIncidentChronology(Long id) {
		return incidentChronologyRepository.deleteIncidentChronology(id);
	}

	@Override
	@Transactional
	public boolean saveIncidentChronology(IncidentChronology chronology) {
		
		if (incidentChronologyRepository.saveIncidentChronology(chronology)) {
			Incident incident = incidentChronologyRepository.getIncidentOfChronology(chronology.getIncident().getId());
			if (incident.getStatus().equals("Closed")) {
				WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
				MailService mailService = (MailService) wac.getBean("mailService");
				mailService.send(incident, appProperties, Type.INCIDENTCHRONOLOGYSTART);
			}
			return true;
		}
		return false;
	}

	@Override
	public IncidentChronology getIncidentChronology(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

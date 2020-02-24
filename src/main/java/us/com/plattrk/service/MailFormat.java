package us.com.plattrk.service;

import us.com.plattrk.api.model.Incident;

public interface MailFormat {

	public String generateBodyFormat(boolean DailyReport);

	public String generateSubjectFormat(Mail.Type type);

	public void initialize(Incident incident);
	
}


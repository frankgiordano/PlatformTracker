package us.com.plattrk.service;

import us.com.plattrk.api.model.Incident;

public interface MailFormat {

	String generateBodyFormat(boolean DailyReport);
	String generateSubjectFormat(Mail.Type type);
	void initialize(Incident incident);
	
}


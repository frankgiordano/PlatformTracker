package us.com.plattrk.api.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.com.plattrk.service.IncidentServiceImpl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JsonDateSerializer extends JsonSerializer<Date> {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
	private static Logger log = LoggerFactory.getLogger(IncidentServiceImpl.class);

	public void serialize(Date date, JsonGenerator gen, SerializerProvider provider)

	throws IOException, JsonProcessingException {

		synchronized(dateFormat) {
			log.debug("inside JSONDATESERIALIZER");
			String formattedDate = dateFormat.format(date);
			log.debug(dateFormat.getTimeZone().toString());
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			log.debug(formattedDate);

			gen.writeString(formattedDate);
		}

	}
	

}

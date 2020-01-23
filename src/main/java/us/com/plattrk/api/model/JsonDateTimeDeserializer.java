package us.com.plattrk.api.model;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonDateTimeDeserializer extends JsonDeserializer<Date>
{
	private static Logger log = LoggerFactory.getLogger(JsonDateTimeDeserializer.class);
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
    public Date deserialize(JsonParser jsonparser,
            DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {
    	
//		String formattedDate = dateFormat.format(date);
////		log.error(dateFormat.getTimeZone().toString());
//		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    	
    	
    	synchronized(format) {
    		String date = jsonparser.getText();
    		log.error("date = " + date);
 //   		format.setTimeZone(zone);
 //     	log.error("Deserialize date = " + date );
    		log.error("TZ = " + format.getTimeZone().toString());
    		log.error("TZ = " + format.toString());
    		format.setTimeZone(TimeZone.getTimeZone("UTC"));
    		log.error("TZ = " + format.getTimeZone().toString());
    		try {
        	
            return format.parse(date);
    		} catch (ParseException e) {
    			throw new RuntimeException(e);
    		}

        }
    }
}

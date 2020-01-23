package us.com.plattrk.api.model;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonDateDeserializer extends JsonDeserializer<Date>
{
	private static Logger log = LoggerFactory.getLogger(JsonDateDeserializer.class);
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
    public Date deserialize(JsonParser jsonparser,
            DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {
    	
    	synchronized(format) {
    		String date = jsonparser.getText();
 //     	log.error("Deserialize date = " + date );
    		try {
        	
            return format.parse(date);
    		} catch (ParseException e) {
    			throw new RuntimeException(e);
    		}

        }
    }
}

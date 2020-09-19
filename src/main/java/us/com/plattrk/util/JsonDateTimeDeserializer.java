package us.com.plattrk.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JsonDateTimeDeserializer extends JsonDeserializer<Date> {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public Date deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException {
        Date date = null;
        String dateString = jsonparser.getText();
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}

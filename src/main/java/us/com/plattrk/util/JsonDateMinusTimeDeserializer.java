package us.com.plattrk.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonDateMinusTimeDeserializer extends JsonDeserializer<Date> {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public Date deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException {
        Date date = null;
        String dateString = jsonparser.getText();
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}

package us.com.plattrk.util;

import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component(value = "MailUtil")
public class MailUtil {

    public static String getAllEmailAddresses(List<String> allEmailAddresses) {
        StringBuilder buffer;
        Iterator<String> iterator = allEmailAddresses.iterator();
        if (allEmailAddresses.size() > 1) {
            buffer = new StringBuilder(iterator.next());
            while (iterator.hasNext()) {
                buffer.append(",").append(iterator.next());
            }
            return buffer.toString();
        } else {
            return allEmailAddresses.get(0);
        }
    }

}

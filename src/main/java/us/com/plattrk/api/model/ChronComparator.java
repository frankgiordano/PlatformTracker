package us.com.plattrk.api.model;

import java.util.Comparator;

public class ChronComparator implements Comparator<IncidentChronology> {

    public int compare(IncidentChronology c1, IncidentChronology c2) {
        return c1.getDateTime().compareTo(c2.getDateTime());
    }

}

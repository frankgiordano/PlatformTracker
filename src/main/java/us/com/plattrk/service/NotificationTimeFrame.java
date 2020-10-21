package us.com.plattrk.service;

import java.util.Calendar;

public class NotificationTimeFrame {

    private boolean onHours;

    public NotificationTimeFrame(boolean onHours) {
        this.onHours = onHours;
    }

    public boolean isWeekEnd() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            return true;
        }

        return false;
    }

    public boolean isWeekDay() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.TUESDAY ||
                dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.THURSDAY
                || dayOfWeek == Calendar.FRIDAY) {
            return true;
        }

        return false;
    }

    public void checkWeekDayAfterHours() {
        Calendar c = Calendar.getInstance();
        if ( (c.get(Calendar.HOUR_OF_DAY) >= 21 && c.get(Calendar.HOUR_OF_DAY) <= 24) ||
                (c.get(Calendar.HOUR_OF_DAY) >= 0 && c.get(Calendar.HOUR_OF_DAY) <= 8) ) {
            setOnHours(false);
        }
    }

    public void checkWeekEndAfterHours() {
        Calendar c = Calendar.getInstance();
        if ( (c.get(Calendar.HOUR_OF_DAY) >= 18 && c.get(Calendar.HOUR_OF_DAY) <= 24) ||
                (c.get(Calendar.HOUR_OF_DAY) >= 0 && c.get(Calendar.HOUR_OF_DAY) <= 8) ) {
            setOnHours(false);
        }
    }

    public boolean isOnHours() {
        return onHours;
    }

    public void setOnHours(boolean onHours) {
        this.onHours = onHours;
    }

}

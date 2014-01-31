package org.kuali.student.myplan.utils;

import java.util.List;

/**
 * Calendar Util gives the Day of Week values, short and Long Names
 * Lists in this class are populated through spring beans.
 */
public class CalendarUtil {

    /**
     * list of days are "SUNDAY", "MONDAY" etc..
     */
    private List<String> days;

    /**
     * list of shortNames are "Su", "Mo" etc..
     */
    private List<String> shortNames;


    public CalendarUtil(List<String> days, List<String> shortNames) {
        this.days = days;
        this.shortNames = shortNames;
    }

    /**
     * Equivalent short name for the day is returned:
     * Eg: SUNDAY --> Su
     *
     * @param dayOfWeek
     * @return shortName
     */
    public String getShortName(String dayOfWeek) {
        if (shortNames != null && days != null) {
            int dayIndex = days.indexOf(dayOfWeek.toUpperCase());
            if (dayIndex > -1) {
                return shortNames.get(dayIndex);
            }
        }
        return null;
    }

    /**
     * takes index value of day of week in Calendar
     * eg: Calendar.SUNDAY --> 1 is passed as param and the short name returned i.e. Su
     *
     * @param calendarDayOfWeek
     * @return shortName
     */
    public String getShortName(int calendarDayOfWeek) {
        if (shortNames != null) {
            if (calendarDayOfWeek > 0) {
                return shortNames.get(calendarDayOfWeek - 1);
            }
        }
        return null;
    }

    /**
     * takes index value of day of week in Calendar
     * eg: Calendar.SUNDAY --> 1 is passed as param and the day returned i.e. Sunday
     *
     * @param calendarDayOfWeek
     * @return shortName
     */
    public String getDay(int calendarDayOfWeek) {
        if (days != null) {
            if (calendarDayOfWeek > 0) {
                return days.get(calendarDayOfWeek - 1);
            }
        }
        return null;
    }

    /**
     * takes Day of week and returns the index of it according to java util Calendar
     * eg: Sunday --> 1
     *
     * @param dayOfWeek
     * @return shortName
     */
    public int getDayValue(String dayOfWeek) {
        if (shortNames != null && days != null) {
            return days.indexOf(dayOfWeek.toUpperCase()) + 1;
        }
        return -1;
    }


}

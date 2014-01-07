package com.sigmasys.kuali.ksa.util;

import org.apache.commons.lang.time.DateUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * CalendarUtils
 *
 * @author Michael Ivanov
 */
public class CalendarUtils {

    // DatatypeFactory creates new javax.xml.datatype Objects that map XML to/from Java Objects.
    private static DatatypeFactory datatypeFactory = null;

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(
                    "Error while trying to obtain a new instance of DatatypeFactory", e);
        }
    }

    private CalendarUtils() {
    }

    /**
     * Safely compares two dates. Checks if the first date is before the second date.
     * Ignores comparison if either or both of the dates are <code>null</code>.
     *
     * @param date      A date to compare.
     * @param compareTo A date to compare to.
     * @return <code>true</code> if "date" is before "compareTo", <code>false</code> otherwise.
     */
    public static boolean isBefore(Date date, Date compareTo) {
        return (date != null) && (compareTo != null) && date.before(compareTo);
    }

    /**
     * Safely compares two dates. Checks if the first date is before or equals to the second date.
     * Ignores comparison if either or both of the dates are <code>null</code>.
     *
     * @param date      A date to compare.
     * @param compareTo A date to compare to.
     * @return <code>true</code> if "date" is before or equals to "compareTo", <code>false</code> otherwise.
     */
    public static boolean isBeforeOrEquals(Date date, Date compareTo) {
        return (date != null) && (compareTo != null) &&
                (date.before(compareTo) || DateUtils.isSameInstant(date, compareTo));
    }

    /**
     * Safely compares two dates. Checks if the first date is after the second date.
     * Ignores comparison if either or both of the dates are <code>null</code>.
     *
     * @param date      A date to compare.
     * @param compareTo A date to compare to.
     * @return <code>true</code> if "date" is after "compareTo", <code>false</code> otherwise.
     */
    public static boolean isAfter(Date date, Date compareTo) {
        return (date != null) && (compareTo != null) && date.after(compareTo);
    }

    /**
     * Safely compares two dates. Checks if the first date is after or equals to the second date.
     * Ignores comparison if either or both of the dates are <code>null</code>.
     *
     * @param date      A date to compare.
     * @param compareTo A date to compare to.
     * @return <code>true</code> if "date" is after or equals "compareTo", <code>false</code> otherwise.
     */
    public static boolean isAfterOrEquals(Date date, Date compareTo) {
        return (date != null) && (compareTo != null) &&
                (date.after(compareTo) || DateUtils.isSameInstant(date, compareTo));
    }

    // Converts a java.util.Date into an instance of XMLGregorianCalendar
    public static XMLGregorianCalendar toXmlGregorianCalendar(Date date, boolean removeTime) {
        if (date != null) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setLenient(false);
            gc.setTime(date);
            if (removeTime) {
                gc = removeTime(gc);
            }
            return datatypeFactory.newXMLGregorianCalendar(gc);
        }
        return null;
    }

    // Converts a java.util.Date into an instance of XMLGregorianCalendar
    public static XMLGregorianCalendar toXmlGregorianCalendar(Date date) {
        return toXmlGregorianCalendar(date, false);
    }

    // Converts an XMLGregorianCalendar to an instance of java.util.Date
    public static Date toDate(XMLGregorianCalendar calendar) {
        return toDate(calendar, false);
    }

    public static Date toDate(XMLGregorianCalendar calendar, boolean removeTime) {
        Date date = null;
        if (calendar != null) {
            date = calendar.toGregorianCalendar().getTime();
            if (removeTime) {
                date = removeTime(date);
            }
        }
        return date;
    }

    public static GregorianCalendar removeTime(GregorianCalendar calendar) {
        // Remove the hours, minutes, seconds and milliseconds.
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // Return the date again.
        return calendar;
    }

    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date addWorkingDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        for (int i = 0; i < days; i++) {
            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SATURDAY:
                    calendar.add(Calendar.DATE, 3);
                    break;
                case Calendar.SUNDAY:
                    calendar.add(Calendar.DATE, 2);
                    break;
                default:
                    calendar.add(Calendar.DATE, 1);
            }
        }

        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SATURDAY:
                calendar.add(Calendar.DATE, 2);
                break;
            case Calendar.SUNDAY:
                calendar.add(Calendar.DATE, 1);
        }

        return calendar.getTime();
    }

    public static Date setTime(Date date, int hours, int minutes, int seconds, int millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, seconds);
        cal.set(Calendar.MILLISECOND, millis);
        return cal.getTime();
    }

    public static int getWorkingDaysBetween(Date fromDate, Date toDate) {

        if (fromDate == null || toDate == null) {
            return 0;
        }

        if (fromDate.after(toDate)) {
            Date temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }

        Calendar calFrom = Calendar.getInstance();
        calFrom.setTime(fromDate);
        Calendar calTo = Calendar.getInstance();
        calTo.setTime(toDate);

        int iNoOfWorkingDays = 0;
        do {
            if (calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                    calFrom.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                iNoOfWorkingDays += 1;
            }
            calFrom.add(Calendar.DATE, 1);
        } while (calFrom.getTimeInMillis() < calTo.getTimeInMillis());

        return iNoOfWorkingDays;
    }

    public static Integer getCalendarDaysBetween(Date fromDate, Date toDate) {
        Calendar calFrom = Calendar.getInstance();
        calFrom.setTime(fromDate);
        Calendar calTo = Calendar.getInstance();
        calTo.setTime(toDate);
        int days = 0;
        while (calFrom.before(calTo)) {
            calFrom.add(Calendar.DAY_OF_MONTH, 1);
            days++;
        }
        return days;
    }

    public static Date addCalendarDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static Date addCalendarMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static Date getFirstDateOfYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return removeTime(cal.getTime());
    }

    public static Date getLastDateOfYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, 11); // 11 = december
        cal.set(Calendar.DAY_OF_MONTH, 31); // new years eve
        return removeTime(cal.getTime());
    }

}

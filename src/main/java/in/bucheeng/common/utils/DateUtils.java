/*
 *  @version     1.0, Dec 12, 2011
 *  @author sunny
 */
package in.bucheeng.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    public final static String    PATTERN_DDMMYYYY = "ddMMyyyy";
    private final static TimeZone IST              = TimeZone.getTimeZone("IST");
    public static final int       MINUTES_IN_A_DAY = 24 * 60;
    public static TimeZone        DEFAULT_TZ       = IST;

    public static enum TextRange {
        TODAY,
        YESTERDAY,
        LAST_WEEK,
        LAST_MONTH,
        THIS_MONTH,
        LAST_7_DAYS,
        LAST_30_DAYS,
        LAST_60_DAYS,
        LAST_90_DAYS,
        LAST_QUARTER,
        THIS_QUARTER
    }

    public static Date stringToDate(String date, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(date);
        } catch (ParseException e) {
        }
        return null;
    }

    public static String dateToString(Date date, String pattern) {
        if (date != null && hasText((CharSequence) pattern)) {
            DateFormat format = new SimpleDateFormat(pattern);
            return format.format(date);
        } else {
            return null;
        }
    }

    public static boolean hasText(CharSequence str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    public static boolean between(Date dateTime, DateRange dateRange) {
        Calendar givenTime = Calendar.getInstance();
        givenTime.setTime(dateTime);
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(dateRange.getStart());
        if (givenTime.after(startTime)) {
            Calendar endTime = Calendar.getInstance();
            endTime.setTime(dateRange.getEnd());
            return givenTime.before(endTime);
        }
        return false;
    }

    public static boolean isPastTime(Date input) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTime(getCurrentTime());
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTime(input);
        return inputTime.before(currentTime);
    }

    public static boolean isFutureTime(Date input) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTime(getCurrentTime());
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTime(input);
        return inputTime.after(currentTime);
    }

    public static boolean isFirstDayOfMonth(Date input) {
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTime(input);
        return inputTime.get(Calendar.DATE) == 1;
    }

    public static Date clearTime(Date dateTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date clearDate(Date dateTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);
        cal.clear(Calendar.YEAR);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static Date getCurrentTime() {
        return new Date();
    }

    public static Date getCurrentDate() {
        Calendar now = Calendar.getInstance();
        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }

    public static boolean isSameDay(Date d1, Date d2) {
        return org.apache.commons.lang.time.DateUtils.isSameDay(d1, d2);
    }

    /**
     * Usage Example : For date 5 Feb 2011 enter year = 2011, month = 2, and day = 5
     * 
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Date createDate(int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(year, month - 1, day, 0, 0, 0);
        return date.getTime();
    }

    public static Date addToDate(Date date, int type, int noOfUnits) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(type, noOfUnits);
        return calendar.getTime();
    }

    /**
     * Limit a date's resolution. For example, the date <code>2004-09-21 13:50:11</code> will be changed to
     * <code>2004-09-01 00:00:00</code> when using <code>Resolution.MONTH</code>.
     * 
     * @param resolution The desired resolution of the date to be returned
     * @return the date with all values more precise than <code>resolution</code> set to 0 or 1
     */
    public static Date round(Date date, Resolution resolution) {
        return new Date(round(date.getTime(), resolution));
    }

    public static long round(long time, Resolution resolution) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(new Date(time));

        if (resolution == Resolution.YEAR) {
            cal.set(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.MONTH) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.WEEK) {
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.QUARTER) {
            int quarter = (cal.get(Calendar.MONTH) / 3);
            switch (quarter) {
                case 3:
                    cal.set(Calendar.MONTH, Calendar.OCTOBER);
                    break;
                case 0:
                    cal.set(Calendar.MONTH, Calendar.JANUARY);
                    break;
                case 1:
                    cal.set(Calendar.MONTH, Calendar.APRIL);
                    break;
                case 2:
                    cal.set(Calendar.MONTH, Calendar.JULY);
                    break;
            }
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.DAY) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.HOUR) {
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.MINUTE) {
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.SECOND) {
            cal.set(Calendar.MILLISECOND, 0);
        } else if (resolution == Resolution.MILLISECOND) {
            // don't cut off anything
        } else {
            throw new IllegalArgumentException("unknown resolution " + resolution);
        }
        return cal.getTime().getTime();
    }

    /**
     * Specifies the time granularity.
     */
    public enum Resolution {
        YEAR(946080000000L),
        QUARTER(7776000000L),
        MONTH(2592000000L),
        WEEK(7 * 24 * 60 * 60 * 1000),
        DAY(24 * 60 * 60 * 1000),
        HOUR(60 * 60 * 1000),
        MINUTE(60 * 1000),
        SECOND(1000),
        MILLISECOND(1);

        private final long milliseconds;

        private Resolution(long milliseconds) {
            this.milliseconds = milliseconds;
        }

        public long milliseconds() {
            return milliseconds;
        }
    }

    public static final class Interval {
        private final TimeUnit timeUnit;
        private final long     period;

        public Interval(TimeUnit timeUnit, long period) {
            this.timeUnit = timeUnit;
            this.period = period;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }

        public long getPeriod() {
            return period;
        }

        public int toMinutes() {
            return (int) timeUnit.toMinutes(period);
        }

    }

    public static DateRange getFutureInterval(Date startTime, Interval interval) {
        Date endTime = addToDate(startTime, Calendar.MINUTE, interval.toMinutes());
        return new DateRange(startTime, endTime);
    }

    public static DateRange getPastInterval(Date endTime, Interval interval) {
        Date startTime = addToDate(endTime, Calendar.MINUTE, -interval.toMinutes());
        return new DateRange(startTime, endTime);
    }

    public static DateRange getDayRange(Date anytime) {
        Date startTime = round(anytime, Resolution.DAY);
        return new DateRange(startTime, DateUtils.addToDate(startTime, Calendar.DATE, 1));
    }

    public static DateRange getLastDayRange() {
        return getDayRange(DateUtils.addToDate(DateUtils.getCurrentTime(), Calendar.DATE, -1));
    }

    public static DateRange getDaysRange(Date anytime, int days) {
        Date startTime = round(anytime, Resolution.DAY);
        return new DateRange(startTime, DateUtils.addToDate(startTime, Calendar.DATE, days));
    }

    public static DateRange getLastDaysRange(int pastDays) {
        return getDaysRange(DateUtils.addToDate(DateUtils.getCurrentTime(), Calendar.DATE, -pastDays), pastDays);
    }

    public static String getWeekDayName(int day) {
        String[] namesOfDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
        if (day < namesOfDays.length) {
            return namesOfDays[day];
        }
        return null;
    }

    public static String getMonthName(int month) {
        String[] monthNames = new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
        if (month < monthNames.length) {
            return monthNames[month];
        }
        return null;
    }

    public static class DateRange {

        private Date      start;

        private Date      end;

        private TextRange textRange;

        public DateRange() {
        }

        public DateRange(Date start, Date end) {
            this.start = start;
            this.end = end;
        }

        public DateRange(TextRange textRange) {
            setTextRange(textRange);
        }

        public Date getStart() {
            if (textRange != null) {
                setTextRange(textRange);
            }
            return start;
        }

        public Date getEnd() {
            if (textRange != null) {
                setTextRange(textRange);
            }
            return end;
        }

        public void setStart(Date start) {
            this.start = start;
        }

        public void setEnd(Date end) {
            this.end = end;
        }

        public int getDuration(Resolution resolution) {
            return getStart() != null && getEnd() != null ? diff(getStart(), getEnd(), resolution) : 0;
        }

        @Override
        public String toString() {
            return getStart() + " - " + getEnd() + "-" + (textRange == null ? "" : textRange);
        }

        public TextRange getTextRange() {
            return textRange;
        }

        public void setTextRange(TextRange textRange) {
            if (textRange != null) {
                switch (textRange) {
                    case TODAY:
                        start = round(getCurrentTime(), Resolution.DAY);
                        end = addToDate(start, Calendar.DATE, 1);
                        break;
                    case YESTERDAY:
                        end = round(getCurrentTime(), Resolution.DAY);
                        start = addToDate(end, Calendar.DATE, -1);
                        break;
                    case LAST_WEEK:
                        end = round(getCurrentTime(), Resolution.WEEK);
                        start = addToDate(end, Calendar.DATE, -7);
                        break;
                    case LAST_MONTH:
                        end = round(getCurrentTime(), Resolution.MONTH);
                        start = addToDate(end, Calendar.MONTH, -1);
                        break;
                    case THIS_MONTH:
                        start = round(getCurrentTime(), Resolution.MONTH);
                        end = addToDate(start, Calendar.MONTH, 1);
                        break;
                    case LAST_7_DAYS:
                        end = round(getCurrentTime(), Resolution.MILLISECOND);
                        start = addToDate(end, Calendar.DATE, -7);
                        break;
                    case LAST_30_DAYS:
                        end = round(getCurrentTime(), Resolution.MILLISECOND);
                        start = addToDate(end, Calendar.DATE, -30);
                        break;
                    case LAST_60_DAYS:
                        end = round(getCurrentTime(), Resolution.MILLISECOND);
                        start = addToDate(end, Calendar.DATE, -60);
                        break;
                    case LAST_90_DAYS:
                        end = round(getCurrentTime(), Resolution.MILLISECOND);
                        start = addToDate(end, Calendar.DATE, -90);
                        break;
                    case LAST_QUARTER:
                        end = round(getCurrentTime(), Resolution.QUARTER);
                        start = addToDate(end, Calendar.MONTH, -3);
                        break;
                    case THIS_QUARTER:
                        start = round(getCurrentTime(), Resolution.QUARTER);
                        end = addToDate(start, Calendar.MONTH, 3);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid value for textRange");
                }
            }
            this.textRange = textRange;
        }
    }

    /**
     * This method can find a time embedded in a string in the following formats : hhmm, hh:mm, h:m, h:mm, hh:m (1 space
     * after/before : is also accepted) This method runs faster than the parse() method of Java
     * 
     * @param token
     * @return the date object
     */

    public static Date parseTime(String token) {
        if (token == null || "".equals(token)) {
            return null;
        }

        Calendar cal = new GregorianCalendar();
        cal.clear();

        char[] ctoken = token.toCharArray();
        StringBuilder hours = new StringBuilder(2);
        StringBuilder mins = new StringBuilder(2);

        if (token.indexOf(":") < 0) {
            for (int i = 0; i < ctoken.length; i++) {
                if (Character.isDigit(ctoken[i]) && (i + 1 < ctoken.length && Character.isDigit(ctoken[i + 1]))) {
                    if ((i + 2 < ctoken.length && Character.isDigit(ctoken[i + 2])) && (i + 3 < ctoken.length && Character.isDigit(ctoken[i + 3]))) {
                        hours.append(ctoken[i]).append(ctoken[i + 1]);
                        mins.append(ctoken[i + 2]).append(ctoken[i + 3]);
                    }
                }
            }
        } else {
            for (int i = 0; i < ctoken.length; i++) {
                if (ctoken[i] == ':') {
                    if (i - 1 >= 0 && Character.isDigit(ctoken[i - 1])) {
                        if (i - 2 >= 0 && Character.isDigit(ctoken[i - 2])) {
                            hours.append(ctoken[i - 2]).append(ctoken[i - 1]);
                        } else {
                            hours.append(ctoken[i - 1]);
                        }
                    } else {
                        if (i - 2 >= 0 && Character.isDigit(ctoken[i - 2])) {
                            if (i - 3 >= 0 && Character.isDigit(ctoken[i - 3])) {
                                hours.append(ctoken[i - 3]).append(ctoken[i - 2]);
                            } else {
                                hours.append(ctoken[i - 2]);
                            }
                        }
                    }

                    if (i + 1 < ctoken.length && Character.isDigit(ctoken[i + 1])) {
                        if (i + 2 < ctoken.length && Character.isDigit(ctoken[i + 2])) {
                            mins.append(ctoken[i + 1]).append(ctoken[i + 2]);
                        } else {
                            mins.append(ctoken[i + 1]);
                        }
                    } else {
                        if (i + 2 < ctoken.length && Character.isDigit(ctoken[i + 2])) {
                            if (i + 3 < ctoken.length && Character.isDigit(ctoken[i + 3])) {
                                mins.append(ctoken[i + 2]).append(ctoken[i + 3]);
                            } else {
                                mins.append(ctoken[i + 2]);
                            }
                        }
                    }
                    break;
                }
            }
        }
        try {
            int hrs = Integer.parseInt(hours.toString());
            int minutes = Integer.parseInt(mins.toString());
            if ((token.contains("pm") || token.contains("PM")) && hrs != 12) {
                hrs += 12;
                hrs %= 24;
            }
            if ((token.contains("am") || token.contains("AM")) && hrs == 12) {
                hrs = 0;
            }

            cal.set(Calendar.HOUR_OF_DAY, hrs);
            cal.set(Calendar.MINUTE, minutes);
        } catch (NumberFormatException e) {
            return null;
        }

        return cal.getTime();
    }

    public static int diff(Date date1, Date date2, Resolution resolution) {
        long diff = Math.abs(date1.getTime() - date2.getTime());
        return Math.round(diff / resolution.milliseconds());
    }

    public static int getCurrentHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    public static Date mergeDateAndTime(Date date, Date time) {
        Calendar outcome = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        outcome.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        outcome.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        outcome.set(Calendar.DATE, calendar.get(Calendar.DATE));
        calendar.setTime(time);
        outcome.set(Calendar.HOUR, calendar.get(Calendar.HOUR));
        outcome.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        outcome.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
        return outcome.getTime();
    }

    /**
     * @return
     */
    public static Date getCurrentDayTime() {
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.MONTH);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.clear(Calendar.YEAR);
        return cal.getTime();
    }
}

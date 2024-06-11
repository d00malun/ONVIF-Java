package be.teletask.onvif.models;

public class OnvifDateAndTime {

    /**
     * If the system time and date are set manually or by NTP
     */
    private String dateTimeType;

    /**
     * Daylight savings on or off.
     */
    private boolean dayLightSavings;

    /**
     * TimeZone formatted according to [IEEE 1003.1]. (Optional)
     */
    private String timeZone;

    /**
     * The time and date in UTC. (Optional)
     */
    private DateTime utcDateTime;

    /**
     * The local time and date of the device. (Optional)
     */
    private DateTime localDateTime;


    public String getDateTimeType() {
        return dateTimeType;
    }

    public void setDateTimeType(String dateTimeType) {
        this.dateTimeType = dateTimeType;
    }

    public boolean isDayLightSavings() {
        return dayLightSavings;
    }

    public void setDayLightSavings(boolean dayLightSavings) {
        this.dayLightSavings = dayLightSavings;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public DateTime getUtcDateTime() {
        return utcDateTime;
    }

    public void setUtcDateTime(DateTime utcDateTime) {
        this.utcDateTime = utcDateTime;
    }

    public DateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(DateTime localDateTime) {
        this.localDateTime = localDateTime;
    }


    @Override
    public String toString() {
        return "OnvifDateAndTime{" +
                "dateTimeType='" + dateTimeType + '\'' +
                ", dayLightSavings=" + dayLightSavings +
                ", timeZone='" + timeZone + '\'' +
                ", utcDateTime=" + utcDateTime +
                ", localDateTime=" + localDateTime +
                '}';
    }

    public static class DateTime {
        private final int year;
        private final int month;
        private final int day;
        private final int hour;
        private final int minute;
        private final int second;

        public DateTime(int year, int month, int day, int hour, int minute, int second) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }

        public int getSecond() {
            return second;
        }

        @Override
        public String toString() {
            return "DateTime{" +
                    "year=" + year +
                    ", month=" + month +
                    ", day=" + day +
                    ", hour=" + hour +
                    ", minute=" + minute +
                    ", second=" + second +
                    '}';
        }
    }
}

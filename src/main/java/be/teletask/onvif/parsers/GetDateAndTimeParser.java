package be.teletask.onvif.parsers;

import be.teletask.onvif.models.OnvifDateAndTime;
import be.teletask.onvif.responses.OnvifResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

public class GetDateAndTimeParser extends OnvifParser<OnvifDateAndTime> {

    private static final String KEY_DATE_TIME_TYPE = "DateTimeType";
    private static final String KEY_DAYLIGHT_SAVINGS = "DaylightSavings";
    private static final String KEY_TIME_ZONE = "TZ";
    private static final String KEY_UTC_DATE_TIME = "UTCDateTime";
    private static final String KEY_LOCAL_DATE_TIME = "LocalDateTime";
    private static final String KEY_YEAR = "Year";
    private static final String KEY_MONTH = "Month";
    private static final String KEY_DAY = "Day";
    private static final String KEY_HOUR = "Hour";
    private static final String KEY_MINUTE = "Minute";
    private static final String KEY_SECOND = "Second";


    @Override
    public OnvifDateAndTime parse(OnvifResponse response) {
        OnvifDateAndTime onvifDateAndTime = new OnvifDateAndTime();

        try {
            getXpp().setInput(new StringReader(response.getXml()));
            eventType = getXpp().getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = getXpp().getName();
                    if (KEY_DATE_TIME_TYPE.equals(tagName)) {
                        getXpp().next();
                        onvifDateAndTime.setDateTimeType(getXpp().getText());
                    } else if (KEY_DAYLIGHT_SAVINGS.equals(tagName)) {
                        getXpp().next();
                        boolean dayLightSavings = getXpp().getText() != null && getXpp().getText().equalsIgnoreCase("true");
                        onvifDateAndTime.setDayLightSavings(dayLightSavings);
                    } else if (KEY_TIME_ZONE.equals(tagName)) {
                        getXpp().next();
                        onvifDateAndTime.setTimeZone(getXpp().getText());
                    } else if (KEY_UTC_DATE_TIME.equals(tagName)) {
                        getXpp().next();
                        onvifDateAndTime.setUtcDateTime(parseDateTimeInsideTag(KEY_UTC_DATE_TIME));
                    } else if (KEY_LOCAL_DATE_TIME.equals(tagName)) {
                        getXpp().next();
                        onvifDateAndTime.setLocalDateTime(parseDateTimeInsideTag(KEY_LOCAL_DATE_TIME));
                    }
                }
                eventType = getXpp().next();

            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return onvifDateAndTime;
    }

    private OnvifDateAndTime.DateTime parseDateTimeInsideTag(String tag) throws XmlPullParserException, IOException {

        int hour = 0;
        int minute = 0;
        int second = 0;
        int year = 0;
        int month = 0;
        int day = 0;

        while (getXpp().getEventType() != XmlPullParser.END_DOCUMENT &&
                !(getXpp().getEventType() == XmlPullParser.END_TAG && tag.equals(getXpp().getName()))
        ) {

            if (getXpp().getEventType() == XmlPullParser.START_TAG) {
                String tagName = getXpp().getName();
                if (KEY_HOUR.equals(tagName)) {
                    getXpp().next();
                    hour = parseInt(getXpp().getText());
                } else if (KEY_MINUTE.equals(tagName)) {
                    getXpp().next();
                    minute = parseInt(getXpp().getText());
                } else if (KEY_SECOND.equals(tagName)) {
                    getXpp().next();
                    second = parseInt(getXpp().getText());
                } else if (KEY_YEAR.equals(tagName)) {
                    getXpp().next();
                    year = parseInt(getXpp().getText());
                } else if (KEY_MONTH.equals(tagName)) {
                    getXpp().next();
                    month = parseInt(getXpp().getText());
                } else if (KEY_DAY.equals(tagName)) {
                    getXpp().next();
                    day = parseInt(getXpp().getText());
                }
            }
            getXpp().next();

        }
        return new OnvifDateAndTime.DateTime(year, month, day, hour, minute, second);
    }

    private int parseInt(String text) {
        int result = 0;
        try {
            result = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            // ignore and return 0
        }
        return result;
    }
}

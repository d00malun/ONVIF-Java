package be.teletask.onvif.parsers;

import be.teletask.onvif.responses.OnvifResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.StringReader;

public class SetPresetParser extends OnvifParser<String> {

    @Override
    public String parse(OnvifResponse response) {
        String presetToken = null;
        try {
            getXpp().setInput(new StringReader(response.getXml()));
            eventType = getXpp().getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = getXpp().getName();
                    if ("PresetToken".equalsIgnoreCase(tagName)) {
                        getXpp().next();
                        presetToken = getXpp().getText();
                    }
                }
                eventType = getXpp().next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return presetToken;
    }
}

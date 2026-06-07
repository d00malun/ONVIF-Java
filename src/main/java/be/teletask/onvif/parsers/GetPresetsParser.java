package be.teletask.onvif.parsers;

import be.teletask.onvif.models.OnvifPreset;
import be.teletask.onvif.responses.OnvifResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class GetPresetsParser extends OnvifParser<List<OnvifPreset>> {

    @Override
    public List<OnvifPreset> parse(OnvifResponse response) {
        List<OnvifPreset> presets = new ArrayList<>();

        try {
            getXpp().setInput(new StringReader(response.getXml()));
            eventType = getXpp().getEventType();
            String token = null;
            String name = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = getXpp().getName();
                    if ("Preset".equalsIgnoreCase(tagName)) {
                        token = getXpp().getAttributeValue(null, "token");
                    } else if ("Name".equalsIgnoreCase(tagName)) {
                        getXpp().next();
                        name = getXpp().getText();
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    String tagName = getXpp().getName();
                    if ("Preset".equalsIgnoreCase(tagName)) {
                        if (token != null && name != null) {
                            presets.add(new OnvifPreset(token, name));
                        }
                        token = null;
                        name = null;
                    }
                }
                eventType = getXpp().next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return presets;
    }
}

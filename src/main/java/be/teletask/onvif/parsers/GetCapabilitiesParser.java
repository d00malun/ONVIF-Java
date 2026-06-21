package be.teletask.onvif.parsers;

import be.teletask.onvif.OnvifUtils;
import be.teletask.onvif.models.OnvifServices;
import be.teletask.onvif.responses.OnvifResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

/**
 * Parses GetCapabilitiesResponse to extract paths for Device, Media, and PTZ services.
 */
public class GetCapabilitiesParser extends OnvifParser<OnvifServices> {

    @Override
    public OnvifServices parse(OnvifResponse response) {
        OnvifServices path = new OnvifServices();

        try {
            getXpp().setInput(new StringReader(response.getXml()));
            eventType = getXpp().getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = getXpp().getName();
                    if (tagName != null) {
                        if (tagName.equalsIgnoreCase("Device")) {
                            String uri = retrieveXAddrForCategory(getXpp(), "Device");
                            if (!uri.isEmpty()) {
                                path.setDeviceInformationPath(OnvifUtils.getPathFromURL(uri));
                            }
                        } else if (tagName.equalsIgnoreCase("Media")) {
                            String uri = retrieveXAddrForCategory(getXpp(), "Media");
                            if (!uri.isEmpty()) {
                                path.setProfilesPath(OnvifUtils.getPathFromURL(uri));
                                path.setStreamURIPath(OnvifUtils.getPathFromURL(uri));
                            }
                        } else if (tagName.equalsIgnoreCase("PTZ")) {
                            String uri = retrieveXAddrForCategory(getXpp(), "PTZ");
                            if (!uri.isEmpty()) {
                                path.setPtzPath(OnvifUtils.getPathFromURL(uri));
                                path.setPtzSupported(true);
                            }
                        }
                    }
                }

                eventType = getXpp().next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return path;
    }

    private String retrieveXAddrForCategory(XmlPullParser xpp, String categoryName) throws IOException, XmlPullParserException {
        String result = "";
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.END_TAG && xpp.getName() != null && xpp.getName().equalsIgnoreCase(categoryName)) {
                break;
            }
            if (eventType == XmlPullParser.START_TAG && xpp.getName() != null && xpp.getName().equalsIgnoreCase("XAddr")) {
                xpp.next();
                result = xpp.getText();
                break;
            }
            eventType = xpp.next();
        }
        return result;
    }

}

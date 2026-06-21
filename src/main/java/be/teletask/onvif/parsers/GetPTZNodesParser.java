package be.teletask.onvif.parsers;

import be.teletask.onvif.models.OnvifPTZNode;
import be.teletask.onvif.responses.OnvifResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class GetPTZNodesParser extends OnvifParser<List<OnvifPTZNode>> {

    @Override
    public List<OnvifPTZNode> parse(OnvifResponse response) {
        List<OnvifPTZNode> nodes = new ArrayList<>();

        try {
            getXpp().setInput(new StringReader(response.getXml()));
            eventType = getXpp().getEventType();
            String token = null;
            String name = null;
            boolean homeSupported = false;
            boolean zoomSupported = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = getXpp().getName();
                    if ("PTZNode".equalsIgnoreCase(tagName)) {
                        token = getXpp().getAttributeValue(null, "token");
                        name = null;
                        homeSupported = false;
                        zoomSupported = false;
                    } else if ("Name".equalsIgnoreCase(tagName)) {
                        if (token != null && name == null) {
                            getXpp().next();
                            name = getXpp().getText();
                        }
                    } else if ("HomeSupported".equalsIgnoreCase(tagName)) {
                        if (token != null) {
                            getXpp().next();
                            String text = getXpp().getText();
                            if (text != null) {
                                homeSupported = Boolean.parseBoolean(text.trim());
                            }
                        }
                    } else if (tagName != null && tagName.toLowerCase().contains("continuouszoom")) {
                        if (token != null) {
                            zoomSupported = true;
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    String tagName = getXpp().getName();
                    if ("PTZNode".equalsIgnoreCase(tagName)) {
                        if (token != null) {
                            nodes.add(new OnvifPTZNode(token, name, homeSupported, zoomSupported));
                        }
                        token = null;
                        name = null;
                        homeSupported = false;
                        zoomSupported = false;
                    }
                }
                eventType = getXpp().next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return nodes;
    }
}

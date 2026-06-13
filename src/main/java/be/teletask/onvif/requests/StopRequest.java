package be.teletask.onvif.requests;

import be.teletask.onvif.models.OnvifType;

/**
 * Created by Gabor Szanto on 02/05/2020.
 */
public class StopRequest implements OnvifRequest<Void> {
    //Constants
    public static final String TAG = StopRequest.class.getSimpleName();

    //Attributes
    private final Listener<Void> listener;
    private String profileToken;
    private boolean panTilt;
    private boolean zoom;

    //Constructors

    public StopRequest(String profileToken, boolean panTilt, boolean zoom, Listener<Void> listener) {
        this.listener = listener;
        this.profileToken = profileToken;
        this.panTilt = panTilt;
        this.zoom = zoom;
    }

    //Properties

    public Listener<Void> getListener() {
        return listener;
    }

    @Override
    public String getXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<tptz:Stop xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\" xmlns:tt=\"http://www.onvif.org/ver10/schema\">");
        sb.append("<tptz:ProfileToken>").append(profileToken).append("</tptz:ProfileToken>");
        if (panTilt) {
            sb.append("<tptz:PanTilt>true</tptz:PanTilt>");
        }
        if (zoom) {
            sb.append("<tptz:Zoom>true</tptz:Zoom>");
        }
        sb.append("</tptz:Stop>");
        return sb.toString();
    }

    @Override
    public OnvifType getType() {
        return OnvifType.PTZ_STOP;
    }
}

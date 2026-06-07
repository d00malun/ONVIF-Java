package be.teletask.onvif.requests;

import be.teletask.onvif.models.OnvifType;

public class GotoHomePositionRequest implements OnvifRequest<Void> {
    private final Listener<Void> listener;
    private final String profileToken;

    public GotoHomePositionRequest(String profileToken, Listener<Void> listener) {
        this.listener = listener;
        this.profileToken = profileToken;
    }

    @Override
    public Listener<Void> getListener() {
        return listener;
    }

    @Override
    public String getXml() {
        return "<tptz:GotoHomePosition xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
                "<tptz:ProfileToken>" + profileToken + "</tptz:ProfileToken>" +
                "</tptz:GotoHomePosition>";
    }

    @Override
    public OnvifType getType() {
        return OnvifType.PTZ_GOTO_HOME_POSITION;
    }
}

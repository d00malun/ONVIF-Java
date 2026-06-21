package be.teletask.onvif.requests;

import be.teletask.onvif.models.OnvifType;

public class SetHomePositionRequest implements OnvifRequest<Void> {
    private final Listener<Void> listener;
    private final String profileToken;

    public SetHomePositionRequest(String profileToken, Listener<Void> listener) {
        this.listener = listener;
        this.profileToken = profileToken;
    }

    @Override
    public Listener<Void> getListener() {
        return listener;
    }

    @Override
    public String getXml() {
        return "<tptz:SetHomePosition xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
                "<tptz:ProfileToken>" + profileToken + "</tptz:ProfileToken>" +
                "</tptz:SetHomePosition>";
    }

    @Override
    public OnvifType getType() {
        return OnvifType.PTZ_SET_HOME_POSITION;
    }
}

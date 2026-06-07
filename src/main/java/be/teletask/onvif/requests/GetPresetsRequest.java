package be.teletask.onvif.requests;

import be.teletask.onvif.models.OnvifPreset;
import be.teletask.onvif.models.OnvifType;
import java.util.List;

public class GetPresetsRequest implements OnvifRequest<List<OnvifPreset>> {
    private final Listener<List<OnvifPreset>> listener;
    private final String profileToken;

    public GetPresetsRequest(String profileToken, Listener<List<OnvifPreset>> listener) {
        this.listener = listener;
        this.profileToken = profileToken;
    }

    @Override
    public Listener<List<OnvifPreset>> getListener() {
        return listener;
    }

    @Override
    public String getXml() {
        return "<tptz:GetPresets xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
                "<tptz:ProfileToken>" + profileToken + "</tptz:ProfileToken>" +
                "</tptz:GetPresets>";
    }

    @Override
    public OnvifType getType() {
        return OnvifType.PTZ_GET_PRESETS;
    }
}

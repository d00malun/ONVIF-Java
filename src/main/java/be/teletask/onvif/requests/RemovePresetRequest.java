package be.teletask.onvif.requests;

import be.teletask.onvif.models.OnvifType;

public class RemovePresetRequest implements OnvifRequest<Void> {
    private final Listener<Void> listener;
    private final String profileToken;
    private final String presetToken;

    public RemovePresetRequest(String profileToken, String presetToken, Listener<Void> listener) {
        this.listener = listener;
        this.profileToken = profileToken;
        this.presetToken = presetToken;
    }

    @Override
    public Listener<Void> getListener() {
        return listener;
    }

    @Override
    public String getXml() {
        return "<tptz:RemovePreset xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
                "<tptz:ProfileToken>" + profileToken + "</tptz:ProfileToken>" +
                "<tptz:PresetToken>" + presetToken + "</tptz:PresetToken>" +
                "</tptz:RemovePreset>";
    }

    @Override
    public OnvifType getType() {
        return OnvifType.PTZ_REMOVE_PRESET;
    }
}

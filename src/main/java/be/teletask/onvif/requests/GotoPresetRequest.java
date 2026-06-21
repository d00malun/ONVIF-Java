package be.teletask.onvif.requests;

import be.teletask.onvif.models.OnvifType;

public class GotoPresetRequest implements OnvifRequest<Void> {
    private final Listener<Void> listener;
    private final String profileToken;
    private final String presetToken;

    public GotoPresetRequest(String profileToken, String presetToken, Listener<Void> listener) {
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
        return "<tptz:GotoPreset xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
                "<tptz:ProfileToken>" + profileToken + "</tptz:ProfileToken>" +
                "<tptz:PresetToken>" + presetToken + "</tptz:PresetToken>" +
                "</tptz:GotoPreset>";
    }

    @Override
    public OnvifType getType() {
        return OnvifType.PTZ_GOTO_PRESET;
    }
}

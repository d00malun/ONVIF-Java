package be.teletask.onvif.requests;

import be.teletask.onvif.models.OnvifType;

public class SetPresetRequest implements OnvifRequest<String> {
    private final Listener<String> listener;
    private final String profileToken;
    private final String presetName;
    private final String presetToken;

    public SetPresetRequest(String profileToken, String presetName, String presetToken, Listener<String> listener) {
        this.listener = listener;
        this.profileToken = profileToken;
        this.presetName = presetName;
        this.presetToken = presetToken;
    }

    @Override
    public Listener<String> getListener() {
        return listener;
    }

    @Override
    public String getXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<tptz:SetPreset xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">");
        sb.append("<tptz:ProfileToken>").append(profileToken).append("</tptz:ProfileToken>");
        if (presetName != null && !presetName.isEmpty()) {
            sb.append("<tptz:PresetName>").append(presetName).append("</tptz:PresetName>");
        }
        if (presetToken != null && !presetToken.isEmpty()) {
            sb.append("<tptz:PresetToken>").append(presetToken).append("</tptz:PresetToken>");
        }
        sb.append("</tptz:SetPreset>");
        return sb.toString();
    }

    @Override
    public OnvifType getType() {
        return OnvifType.PTZ_SET_PRESET;
    }
}

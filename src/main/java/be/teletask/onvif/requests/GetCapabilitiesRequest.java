package be.teletask.onvif.requests;

import be.teletask.onvif.models.OnvifServices;
import be.teletask.onvif.models.OnvifType;

/**
 * Created for older camera support (GetServices fallback).
 */
public class GetCapabilitiesRequest implements OnvifRequest<OnvifServices> {

    public static final String TAG = GetCapabilitiesRequest.class.getSimpleName();
    private final Listener<OnvifServices> listener;

    public GetCapabilitiesRequest(Listener<OnvifServices> listener) {
        super();
        this.listener = listener;
    }

    public Listener<OnvifServices> getListener() {
        return listener;
    }

    @Override
    public String getXml() {
        return "<GetCapabilities xmlns=\"http://www.onvif.org/ver10/device/wsdl\">" +
                "<Category>All</Category>" +
                "</GetCapabilities>";
    }

    @Override
    public OnvifType getType() {
        return OnvifType.GET_CAPABILITIES;
    }

}

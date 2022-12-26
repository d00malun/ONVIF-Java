package be.teletask.onvif.requests;

import be.teletask.onvif.models.OnvifDateAndTime;
import be.teletask.onvif.models.OnvifType;

public class GetDateAndTimeRequest implements OnvifRequest<OnvifDateAndTime> {

    private final Listener<OnvifDateAndTime> listener;

    public GetDateAndTimeRequest(Listener<OnvifDateAndTime> listener) {
        super();
        this.listener = listener;
    }

    @Override
    public String getXml() {
        return "<GetSystemDateAndTime xmlns=\"http://www.onvif.org/ver10/device/wsdl\" />";
    }

    @Override
    public OnvifType getType() {
        return OnvifType.GET_DATE_AND_TIME;
    }

    @Override
    public Listener<OnvifDateAndTime> getListener() {
        return listener;
    }

}

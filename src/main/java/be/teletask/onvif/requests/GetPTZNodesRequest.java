package be.teletask.onvif.requests;

import be.teletask.onvif.models.OnvifType;
import be.teletask.onvif.models.OnvifPTZNode;
import java.util.List;

public class GetPTZNodesRequest implements OnvifRequest<List<OnvifPTZNode>> {

    private final Listener<List<OnvifPTZNode>> listener;

    public GetPTZNodesRequest(Listener<List<OnvifPTZNode>> listener) {
        this.listener = listener;
    }

    @Override
    public Listener<List<OnvifPTZNode>> getListener() {
        return listener;
    }

    @Override
    public String getXml() {
        return "<tptz:GetNodes xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\"/>";
    }

    @Override
    public OnvifType getType() {
        return OnvifType.PTZ_GET_NODES;
    }
}

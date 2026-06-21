package be.teletask.onvif.requests;

import be.teletask.onvif.models.OnvifType;

/**
 * Created by Gabor Szanto on 02/05/2020.
 */
public class ContinuousMoveRequest implements OnvifRequest<Void> {
    //Constants
    public static final String TAG = ContinuousMoveRequest.class.getSimpleName();

    //Attributes
    private final Listener<Void> listener;
    private String profileToken;
    private Integer timeout;
    private Double velocityX;
    private Double velocityY;
    private Double velocityZ;

    //Constructors

    public ContinuousMoveRequest(String profileToken, Integer timeout, Double velocityX, Double velocityY, Double velocityZ, Listener<Void> listener) {
        this.listener = listener;
        this.profileToken = profileToken;
        this.timeout = timeout;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }

    //Properties

    public Listener<Void> getListener() {
        return listener;
    }

    @Override
    public String getXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<tptz:ContinuousMove xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\" xmlns:tt=\"http://www.onvif.org/ver10/schema\">");
        sb.append("<tptz:ProfileToken>").append(profileToken).append("</tptz:ProfileToken>");
        sb.append("<tptz:Velocity>");
        if ((velocityX != null && Math.abs(velocityX) > 0.0001) || (velocityY != null && Math.abs(velocityY) > 0.0001)) {
            double x = velocityX != null ? velocityX : 0.0;
            double y = velocityY != null ? velocityY : 0.0;
            sb.append("<tt:PanTilt x=\"").append(x).append("\" y=\"").append(y).append("\"></tt:PanTilt>");
        }
        if (velocityZ != null && Math.abs(velocityZ) > 0.0001) {
            sb.append("<tt:Zoom x=\"").append(velocityZ).append("\"></tt:Zoom>");
        }
        sb.append("</tptz:Velocity>");
        if (timeout != null) {
            sb.append("<tptz:Timeout>PT").append(timeout).append("S</tptz:Timeout>");
        }
        sb.append("</tptz:ContinuousMove>");
        return sb.toString();
    }

    @Override
    public OnvifType getType() {
        return OnvifType.PTZ_CONTINUOUS_MOVE;
    }
}

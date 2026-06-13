package be.teletask.onvif.models;

public class OnvifPTZNode {
    private final String token;
    private final String name;
    private final boolean homeSupported;
    private final boolean zoomSupported;

    public OnvifPTZNode(String token, String name, boolean homeSupported, boolean zoomSupported) {
        this.token = token;
        this.name = name;
        this.homeSupported = homeSupported;
        this.zoomSupported = zoomSupported;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public boolean isHomeSupported() {
        return homeSupported;
    }

    public boolean isZoomSupported() {
        return zoomSupported;
    }
}

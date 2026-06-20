package be.teletask.onvif.models;

public class OnvifPreset {
    private final String token;
    private final String name;
    private Double x;
    private Double y;
    private Double zoom;

    public OnvifPreset(String token, String name) {
        this.token = token;
        this.name = name;
    }

    public OnvifPreset(String token, String name, Double x, Double y, Double zoom) {
        this(token, name);
        this.x = x;
        this.y = y;
        this.zoom = zoom;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZoom() {
        return zoom;
    }
}

package be.teletask.onvif.models;

public class OnvifPreset {
    private final String token;
    private final String name;

    public OnvifPreset(String token, String name) {
        this.token = token;
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }
}

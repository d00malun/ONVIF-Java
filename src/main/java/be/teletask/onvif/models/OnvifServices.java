package be.teletask.onvif.models;

/**
 * Created by Tomas Verhelst on 03/09/2018.
 * Copyright (c) 2018 TELETASK BVBA. All rights reserved.
 */
public class OnvifServices {

    //Constants
    public static final String TAG = OnvifServices.class.getSimpleName();
    public static final String ONVIF_PATH_SERVICES = "/onvif/device_service";
    public static final String ONVIF_PATH_DEVICE_INFORMATION = "/onvif/device_service";
    public static final String ONVIF_PATH_PROFILES = "/onvif/device_service";
    public static final String ONVIF_PATH_STREAM_URI = "/onvif/device_service";
    public static final String ONVIF_PATH_PTZ = "/onvif/device_service";

    //Attributes
    private String servicesPath = ONVIF_PATH_SERVICES;
    private String deviceInformationPath = ONVIF_PATH_DEVICE_INFORMATION;
    private String profilesPath = ONVIF_PATH_PROFILES;
    private String streamURIPath = ONVIF_PATH_STREAM_URI;
    private String ptzPath = ONVIF_PATH_PTZ;

    private boolean ptzSupported = false;

    //Constructors
    public OnvifServices() {
    }

    //Properties

    public String getServicesPath() {
        return servicesPath;
    }

    public void setServicesPath(String servicesPath) {
        this.servicesPath = servicesPath;
    }

    public String getDeviceInformationPath() {
        return deviceInformationPath;
    }

    public void setDeviceInformationPath(String deviceInformationPath) {
        this.deviceInformationPath = deviceInformationPath;
    }

    public String getProfilesPath() {
        return profilesPath;
    }

    public void setProfilesPath(String profilesPath) {
        this.profilesPath = profilesPath;
    }

    public String getStreamURIPath() {
        return streamURIPath;
    }

    public void setStreamURIPath(String streamURIPath) {
        this.streamURIPath = streamURIPath;
    }

    public String getPtzPath() {
        return ptzPath;
    }

    public void setPtzPath(String ptzPath) {
        this.ptzPath = ptzPath;
    }

    public boolean isPtzSupported() {
        return ptzSupported;
    }

    public void setPtzSupported(boolean ptzSupported) {
        this.ptzSupported = ptzSupported;
    }

}

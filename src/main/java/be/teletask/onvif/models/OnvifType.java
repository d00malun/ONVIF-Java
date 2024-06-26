package be.teletask.onvif.models;

/**
 * Created by Tomas Verhelst on 04/09/2018.
 * Copyright (c) 2018 TELETASK BVBA. All rights reserved.
 */
public enum OnvifType {
    CUSTOM(""),
    GET_SERVICES("http://www.onvif.org/ver10/device/wsdl"),
    GET_DATE_AND_TIME("http://www.onvif.org/ver10/device/wsdl"),
    GET_DEVICE_INFORMATION("http://www.onvif.org/ver10/device/wsdl"),
    GET_MEDIA_PROFILES("http://www.onvif.org/ver10/media/wsdl"),
    GET_STREAM_URI("http://www.onvif.org/ver10/media/wsdl"),
    GET_SNAPSHOT_URI("http://www.onvif.org/ver10/media/wsdl"),
    CONTINUOUS_MOVE("http://www.onvif.org/ver10/media/wsdl"),
    ABSOLUTE_MOVE("http://www.onvif.org/ver10/media/wsdl"),
    PTZ_STOP("http://www.onvif.org/ver10/media/wsdl"),
    RELATIVE_MOVE("http://www.onvif.org/ver10/media/wsdl");

    public final String namespace;

    OnvifType(String namespace) {
        this.namespace = namespace;
    }

}

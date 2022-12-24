package be.teletask.onvif.parsers;

import be.teletask.onvif.DiscoveryMode;
import be.teletask.onvif.OnvifUtils;
import be.teletask.onvif.models.Device;
import be.teletask.onvif.models.DiscoveryType;
import be.teletask.onvif.models.OnvifDevice;
import be.teletask.onvif.models.UPnPDevice;
import be.teletask.onvif.responses.OnvifResponse;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.*;

/**
 * Created by Tomas Verhelst on 04/09/2018.
 * Copyright (c) 2018 TELETASK BVBA. All rights reserved.
 */
public class DiscoveryParser extends OnvifParser<List<Device>> {

    //Constants
    public static final String TAG = DiscoveryParser.class.getSimpleName();
    private static final String LINE_END = "\r\n";
    private static final String KEY_UPNP_LOCATION = "LOCATION: ";
    private static final String KEY_UPNP_SERVER = "SERVER: ";
    private static final String KEY_UPNP_USN = "USN: ";
    private static final String KEY_UPNP_ST = "ST: ";

    //Attributes
    private DiscoveryMode mode;
    private String hostName;

    //Constructors
    public DiscoveryParser(DiscoveryMode mode) {
        this.mode = mode;
        hostName = "";
    }

    //Methods

    @Override
    public List<Device> parse(OnvifResponse response) {
        List<Device> devices = new ArrayList<>();

        switch (mode) {
            case ONVIF:
                devices.addAll(parseOnvif(response));
                break;
            case UPNP:
                devices.add(parseUPnP(response));
                break;
        }

        return devices;
    }

    private List<Device> parseOnvif(OnvifResponse response) {
        ArrayList<Device> devices = new ArrayList<>();
        try {
            getXpp().setInput(new StringReader(response.getXml()));
            eventType = getXpp().getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG && getXpp().getName().equals("Types")) {
                    getXpp().next();
                    String type = getXpp().getText();

                    DiscoveryType discoveryType = null;
                    for (DiscoveryType t : DiscoveryType.values()) {
                        if (type.toLowerCase().contains(t.type.toLowerCase())) {
                            discoveryType = t;
                            break;
                        }
                    }

                    if (mode.equals(DiscoveryMode.ONVIF) && discoveryType != null) {
                        final OnvifUtils.UriAndScopes uriAndScopes = OnvifUtils.retrieveXAddrsAndScopes(getXpp());
                        devices.add(parseDeviceFromUriAndScopes(uriAndScopes));
                    }
                }

                eventType = getXpp().next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return devices;
    }

    private Device parseUPnP(OnvifResponse response) {
        String header = response.getXml();
        String location = parseUPnPHeader(header, KEY_UPNP_LOCATION);
        String server = parseUPnPHeader(header, KEY_UPNP_SERVER);
        String usn = parseUPnPHeader(header, KEY_UPNP_USN);
        String st = parseUPnPHeader(header, KEY_UPNP_ST);
        return new UPnPDevice(getHostName(), header, location, server, usn, st);
    }

    //Properties

    private String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    private OnvifDevice parseDeviceFromUriAndScopes(OnvifUtils.UriAndScopes uriAndScopes) {
        OnvifDevice device = new OnvifDevice(getHostName());
        updateDeviceFromScopes(device, uriAndScopes);
        updateDeviceFromUri(device, uriAndScopes);
        return device;
    }

    private void updateDeviceFromScopes(OnvifDevice device, OnvifUtils.UriAndScopes uriAndScopes) {
        if (uriAndScopes.getScopes() != null) {
            for (String scope : uriAndScopes.getScopes()) {
                final int indexOf = scope.lastIndexOf("/") + 1;
                if (scope.contains("onvif://www.onvif.org/hardware/")) device.setHardware(scope.substring(indexOf));
                if (scope.contains("onvif://www.onvif.org/location/")) device.setLocation(scope.substring(indexOf));
                if (scope.contains("onvif://www.onvif.org/name/")) device.setName(scope.substring(indexOf));
            }
        }
    }

    private void updateDeviceFromUri(OnvifDevice device, OnvifUtils.UriAndScopes uriAndScopes) {
        String[] uris = uriAndScopes.getUri().split("\\s+");
        for (String address : uris) {
            try {
                final URI url = URI.create(address);
                final String parsedAddress = url.getScheme() + "://" + url.getHost() + (url.getPort() == 0 || url.getPort() == -1 ? "" : ":" + url.getPort()) + url.getPath();
                device.addAddress(parsedAddress);
            } catch (Exception e){
                System.err.println("Failed to parse address: " + address);
            }
        }
    }

    private String parseUPnPHeader(String header, String whatSearch) {
        String result = "";
        int searchLinePos = header.indexOf(whatSearch);
        if (searchLinePos != -1) {
            searchLinePos += whatSearch.length();
            int locColon = header.indexOf(LINE_END, searchLinePos);
            result = header.substring(searchLinePos, locColon);
        }
        return result;
    }


}

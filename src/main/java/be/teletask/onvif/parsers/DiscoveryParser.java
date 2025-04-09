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

    private static final String WARMUP_XML = "" +
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
            "                   xmlns:SOAP-ENC=\"http://www.w3.org/2003/05/soap-encoding\"\n" +
            "                   xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\"\n" +
            "                   xmlns:d=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\"\n" +
            "                   xmlns:dn=\"http://www.onvif.org/ver10/network/wsdl\"\n" +
            "                   xmlns:tds=\"http://www.onvif.org/ver10/device/wsdl\">\n" +
            "<SOAP-ENV:Header>\n" +
            "    <wsa:MessageID>uuid:846a7224-7a5d-4b2d-9f0f-d6ab3cea6ce7</wsa:MessageID>\n" +
            "    <wsa:RelatesTo>uuid:bd64d03b-c701-4b50-a4bc-18c4048971c3</wsa:RelatesTo>\n" +
            "    <wsa:To SOAP-ENV:mustUnderstand=\"true\">http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</wsa:To>\n" +
            "    <wsa:Action SOAP-ENV:mustUnderstand=\"true\">http://schemas.xmlsoap.org/ws/2005/04/discovery/ProbeMatches</wsa:Action>\n" +
            "    <d:AppSequence SOAP-ENV:mustUnderstand=\"true\" MessageNumber=\"175811\" InstanceId=\"1670367710\"></d:AppSequence>\n" +
            "</SOAP-ENV:Header>\n" +
            "<SOAP-ENV:Body>\n" +
            "    <d:ProbeMatches>\n" +
            "        <d:ProbeMatch>\n" +
            "            <wsa:EndpointReference>\n" +
            "                <wsa:Address>urn:uuid:e83a0086-3fdc-4d87-b8c4-83c0a16ad9f4</wsa:Address>\n" +
            "            </wsa:EndpointReference>\n" +
            "            <d:Types>tds:Device dn:NetworkVideoTransmitter</d:Types>\n" +
            "            <d:Scopes>onvif://www.onvif.org/Profile/Streaming onvif://www.onvif.org/Profile/G\n" +
            "                onvif://www.onvif.org/hardware/M2036-LE onvif://www.onvif.org/name/AXIS%20M2036-LE\n" +
            "                onvif://www.onvif.org/Profile/M onvif://www.onvif.org/Profile/T onvif://www.onvif.org/location/\n" +
            "            </d:Scopes>\n" +
            "            <d:XAddrs>http://192.168.0.90/onvif/device_service https://192.168.0.90/onvif/device_service\n" +
            "                http://169.254.209.66/onvif/device_service https://169.254.209.66/onvif/device_service\n" +
            "            </d:XAddrs>\n" +
            "            <d:MetadataVersion>1</d:MetadataVersion>\n" +
            "        </d:ProbeMatch>\n" +
            "    </d:ProbeMatches>\n" +
            "</SOAP-ENV:Body>\n" +
            "</SOAP-ENV:Envelope>";

    //Attributes
    private final DiscoveryMode discoveryMode;
    private final DiscoveryType discoveryType;
    private String hostName;

    //Constructors
    public DiscoveryParser(DiscoveryMode discoveryMode, DiscoveryType discoveryType) {
        this.discoveryMode = discoveryMode;
        this.discoveryType = discoveryType;
        hostName = "";

        // warmup the parser
        parse(new OnvifResponse<>(WARMUP_XML));
    }

    //Methods

    @Override
    public List<Device> parse(OnvifResponse response) {
        List<Device> devices = new ArrayList<>();

        switch (discoveryMode) {
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
                    String typeString = getXpp().getText();
                    if (typeString.toLowerCase().contains(discoveryType.type.toLowerCase())) {
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
                if (scope.contains("onvif://www.onvif.org/hardware/")) device.setHardware(scope.replace("onvif://www.onvif.org/hardware/", ""));
                if (scope.contains("onvif://www.onvif.org/location/")) device.setLocation(scope.substring(indexOf));
                if (scope.contains("onvif://www.onvif.org/name/")) device.setName(scope.replace("onvif://www.onvif.org/name/", ""));
            }
        }
    }

    private void updateDeviceFromUri(OnvifDevice device, OnvifUtils.UriAndScopes uriAndScopes) {
        String[] uris = uriAndScopes.getUri().split("\\s+");
        for (String address : uris) {
            if (address.startsWith("http://[]") || address.startsWith("https://[]")) {
                continue;
            }
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

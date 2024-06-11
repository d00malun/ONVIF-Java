package be.teletask.onvif.models;

import be.teletask.onvif.DiscoveryMode;
import be.teletask.onvif.OnvifXMLBuilder;

/**
 * Created by Tomas Verhelst on 05/09/2018.
 * Copyright (c) 2018 TELETASK BVBA. All rights reserved.
 */
public class DiscoveryPacket extends OnvifPacket {

    //Constants
    public static final String TAG = DiscoveryPacket.class.getSimpleName();
    private static final String LINE_END = "\r\n";
    private static final String DEFAULT_QUERY = "M-SEARCH * HTTP/1.1" + LINE_END +
            "HOST: 239.255.255.250:1900" + LINE_END +
            "MAN: \"ssdp:discover\"" + LINE_END +
            "MX: 1" + LINE_END +
            //"ST: urn:schemas-upnp-org:service:AVTransport:1" + LINE_END + // Use for Sonos
            //"ST: urn:schemas-upnp-org:device:InternetGatewayDevice:1" + LINE_END + // Use for Routes
            "ST: ssdp:all" + LINE_END + // Use this for all UPnP Devices
            LINE_END;

    //Attributes
    private final String uuid;
    private final DiscoveryMode mode;
    private final DiscoveryType discoveryType;

    //Constructors
    public DiscoveryPacket(String uuid) {
        this(uuid, DiscoveryMode.ONVIF, DiscoveryType.DEVICE);
    }

    public DiscoveryPacket(String uuid, DiscoveryMode mode, DiscoveryType type) {
        this.uuid = uuid;
        this.mode = mode;
        this.discoveryType = type;
    }

    //Properties
    @Override
    public byte[] getData() {
        String data = "";
        if (mode.equals(DiscoveryMode.ONVIF)) {
            StringBuilder builder = new StringBuilder();
            String header = OnvifXMLBuilder.getDiscoverySoapHeader(uuid);
            builder.append(header);
            builder.append(OnvifXMLBuilder.getDiscoverySoapBody(discoveryType.type));
            builder.append(OnvifXMLBuilder.getEnvelopeEnd());
            data = builder.toString();
        } else if (mode.equals(DiscoveryMode.UPNP)) {
            data = DEFAULT_QUERY;
        }

        return data.getBytes();
    }

}

package be.teletask.onvif;

import be.teletask.onvif.listeners.DiscoveryCallback;
import be.teletask.onvif.models.DiscoveryType;
import be.teletask.onvif.parsers.DiscoveryParser;
import be.teletask.onvif.responses.OnvifResponse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;

/**
 * Created by Tomas Verhelst on 05/09/2018.
 * Copyright (c) 2018 TELETASK BVBA. All rights reserved.
 */
public class DiscoveryThread extends Thread {

    //Constants
    public static final String TAG = DiscoveryThread.class.getSimpleName();

    //Attributes
    private DatagramSocket server;
    private int timeout;
    private DiscoveryParser parser;
    private DiscoveryCallback callback;

    //Constructors
    DiscoveryThread(DatagramSocket server, int timeout, DiscoveryMode mode, DiscoveryType type, DiscoveryCallback callback) {
        super();
        this.server = server;
        this.timeout = timeout;
        this.callback = callback;
        parser = new DiscoveryParser(mode, type);
    }

    @Override
    public void run() {
        try {
            boolean started = false;
            DatagramPacket packet = new DatagramPacket(new byte[4096], 4096);
            server.setSoTimeout(timeout);
            long timerStarted = System.currentTimeMillis();
            while (System.currentTimeMillis() - timerStarted < timeout) {
                if (!started) {
                    callback.onDiscoveryStarted();
                    started = true;
                }

                server.receive(packet);
                String response = new String(packet.getData(), 0, packet.getLength());
                InetAddress inetAddress = packet.getAddress();
                String ip = inetAddress.getHostAddress();
                if (inetAddress instanceof Inet6Address) {
                    ip = "[" + ip + "]";
                }
                parser.setHostName(ip);
                callback.onDevicesFound(parser.parse(new OnvifResponse<>(response)));
            }

        } catch (IOException ignored) {
        } finally {
            server.close();
            callback.onDiscoveryFinished();
        }
    }
}

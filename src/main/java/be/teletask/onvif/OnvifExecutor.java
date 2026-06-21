package be.teletask.onvif;

import be.teletask.onvif.listeners.OnvifResponseListener;
import be.teletask.onvif.models.OnvifDevice;
import be.teletask.onvif.models.OnvifServices;
import be.teletask.onvif.parsers.*;
import be.teletask.onvif.requests.OnvifRequest;
import be.teletask.onvif.responses.OnvifResponse;
import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.DispatchingAuthenticator;
import com.burgstaller.okhttp.basic.BasicAuthenticator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import okhttp3.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tomas Verhelst on 03/09/2018.
 * Copyright (c) 2018 TELETASK BVBA. All rights reserved.
 */
public class OnvifExecutor {

    private static final Logger log = LoggerFactory.getLogger(OnvifExecutor.class);

    //Constants
    public static final String TAG = OnvifExecutor.class.getSimpleName();

    //Attributes
    private OkHttpClient client;
    private MediaType reqBodyType;
    private RequestBody reqBody;

    private final Credentials credentials = new Credentials("", "");
    private OnvifResponseListener onvifResponseListener;

    //Constructors

    OnvifExecutor(OnvifResponseListener onvifResponseListener) {
        this.onvifResponseListener = onvifResponseListener;

        DigestAuthenticator digestAuthenticator = new DigestAuthenticator(credentials);
        BasicAuthenticator basicAuthenticator = new BasicAuthenticator(credentials);

        DispatchingAuthenticator authenticator = new DispatchingAuthenticator.Builder()
                .with("digest", digestAuthenticator)
                .with("basic", basicAuthenticator)
                .build();

        Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();

        client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new AuthenticationCacheInterceptor(authCache))
                .authenticator(new CachingAuthenticatorDecorator(authenticator, authCache))
                .build();

        reqBodyType = MediaType.parse("application/soap+xml; charset=utf-8;");
    }

    //Methods

    /**
     * Sends a request to the Onvif-compatible device.
     *
     * @param device
     * @param request
     */
    void sendRequest(OnvifDevice device, OnvifRequest<?> request) {
        credentials.setUserName(device.getUsername());
        credentials.setPassword(device.getPassword());
        reqBody = RequestBody.create(reqBodyType, OnvifXMLBuilder.getSoapHeader(credentials) + request.getXml() + OnvifXMLBuilder.getEnvelopeEnd());
        performXmlRequest(device, request, buildOnvifRequest(device, request));
    }

    /**
     * Clears up the resources.
     */
    void clear() {
        onvifResponseListener = null;
    }

    //Properties

    public void setOnvifResponseListener(OnvifResponseListener onvifResponseListener) {
        this.onvifResponseListener = onvifResponseListener;
    }

    private void performXmlRequest(OnvifDevice device, OnvifRequest<?> request, Request xmlRequest) {
        if (xmlRequest == null) return;

        if (new File("onvif-debug-enable.txt").exists() && log.isInfoEnabled()) {
            log.info("Sending request {} to device {}:\n{}", request.getType(), device.getHostName(), sanitizeXml(request.getXml()));
        }

        client.newCall(xmlRequest)
                .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response xmlResponse) throws IOException {
                        handleResponse(device, request, xmlResponse);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        handleFailure(device, request, e);
                    }
                });
    }

    private void handleResponse(OnvifDevice device, OnvifRequest<?> request, Response xmlResponse) throws IOException {
        OnvifResponse<Object> response = new OnvifResponse(request);
        ResponseBody xmlBody = xmlResponse.body();

        if (xmlBody == null) {
            notifyError(device, request, xmlResponse.code(), "Empty response body");
            return;
        }

        String xmlContent = xmlBody.string();
        if (new File("onvif-debug-enable.txt").exists() && log.isInfoEnabled()) {
            log.info("Received response for request {} from device {}:\n{}", request.getType(), device.getHostName(), sanitizeXml(xmlContent));
        }

        // Check for SOAP Fault (even if HTTP status is 200)
        if (xmlContent.contains(":Fault") || xmlContent.contains("<Fault")) {
            String reason = extractSoapFaultReason(xmlContent);
            notifyError(device, request, xmlResponse.code(), reason);
            return;
        }

        if (xmlResponse.code() == 200) {
            response.setSuccess(true);
            response.setXml(xmlContent);
            parseResponse(device, response);
            return;
        }

        notifyError(device, request, xmlResponse.code(), xmlContent);
    }

    private void handleFailure(OnvifDevice device, OnvifRequest<?> request, IOException e) {
        notifyError(device, request, -1, e.getMessage());
    }

    private void notifyError(OnvifDevice device, OnvifRequest<?> request, int errorCode, String errorMessage) {
        OnvifRequest.OnvifException exception = new OnvifRequest.OnvifException(device, errorCode, errorMessage);
        if (request.getListener() != null) {
            request.getListener().onError(exception);
        }
        if (onvifResponseListener != null) {
            onvifResponseListener.onError(exception);
        }
    }

    private void parseResponse(OnvifDevice device, OnvifResponse<Object> response) {
        Object data = null;
        switch (response.request().getType()) {
            case GET_SERVICES:
                OnvifServices path = new GetServicesParser().parse(response);
                device.setPath(path);
                data = path;
                break;
            case GET_CAPABILITIES:
                OnvifServices capPath = new GetCapabilitiesParser().parse(response);
                device.setPath(capPath);
                data = capPath;
                break;
            case GET_DATE_AND_TIME:
                data = new GetDateAndTimeParser().parse(response);
                break;
            case GET_DEVICE_INFORMATION:
                data = new GetDeviceInformationParser().parse(response);
                break;
            case GET_MEDIA_PROFILES:
                data = new GetMediaProfilesParser().parse(response);
                break;
            case GET_STREAM_URI:
            case GET_SNAPSHOT_URI:
                data = new GetMediaStreamParser().parse(response);
                break;
            case PTZ_GET_PRESETS:
                data = new GetPresetsParser().parse(response);
                break;
            case PTZ_SET_PRESET:
                data = new SetPresetParser().parse(response);
                break;
            case PTZ_GET_NODES:
                data = new GetPTZNodesParser().parse(response);
                break;
            default:
                onvifResponseListener.onResponse(device, response);
                break;
        }

        response.request().getListener().onSuccess(device, data);
    }

    private Request buildOnvifRequest(OnvifDevice device, OnvifRequest<?> request) {
        return new Request.Builder()
                .url(getUrlForRequest(device, request))
                .addHeader("Content-Type", "text/xml; charset=utf-8")
                .post(reqBody)
                .build();
    }

    private String getUrlForRequest(OnvifDevice device, OnvifRequest<?> request) {
        String base = device.getHostName();
        String path = getPathForRequest(device, request);

        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }

        try {
            java.net.URL url = new java.net.URL(base);
            int port = url.getPort();
            String portPart = port == -1 ? "" : ":" + port;
            base = url.getProtocol() + "://" + url.getHost() + portPart;
        } catch (java.net.MalformedURLException e) {
            // Fallback: keep original but normalize slashes
            if (base.endsWith("/") && path.startsWith("/")) {
                return base + path.substring(1);
            } else if (!base.endsWith("/") && !path.startsWith("/")) {
                return base + "/" + path;
            }
            return base + path;
        }

        if (base.endsWith("/") && path.startsWith("/")) {
            return base + path.substring(1);
        } else if (!base.endsWith("/") && !path.startsWith("/")) {
            return base + "/" + path;
        }
        return base + path;
    }

    private String getPathForRequest(OnvifDevice device, OnvifRequest<?> request) {
        switch (request.getType()) {
            case GET_SERVICES:
            case GET_CAPABILITIES:
                return device.getPath().getServicesPath();
            case GET_DEVICE_INFORMATION:
                return device.getPath().getDeviceInformationPath();
            case GET_MEDIA_PROFILES:
                return device.getPath().getProfilesPath();
            case GET_STREAM_URI:
                return device.getPath().getStreamURIPath();
            case PTZ_CONTINUOUS_MOVE:
            case PTZ_RELATIVE_MOVE:
            case PTZ_ABSOLUTE_MOVE:
            case PTZ_STOP:
            case PTZ_GET_PRESETS:
            case PTZ_SET_PRESET:
            case PTZ_GOTO_PRESET:
            case PTZ_REMOVE_PRESET:
            case PTZ_GOTO_HOME_POSITION:
            case PTZ_SET_HOME_POSITION:
            case PTZ_GET_NODES:
                return device.getPath().getPtzPath();
        }

        return device.getPath().getServicesPath();
    }

    private String extractSoapFaultReason(String xml) {
        if (xml == null) {
            return "Unknown SOAP Fault";
        }

        // Try to find <Text> (SOAP 1.2)
        String text = extractTagContent(xml, "Text");
        if (text != null && !text.trim().isEmpty()) {
            return text.trim();
        }

        // Try to find <faultstring> (SOAP 1.1)
        String faultString = extractTagContent(xml, "faultstring");
        if (faultString != null && !faultString.trim().isEmpty()) {
            return faultString.trim();
        }

        // Try to find <Reason> as fallback
        String reason = extractTagContent(xml, "Reason");
        if (reason != null) {
            String stripped = reason.replaceAll("<[^>]*>", "").trim();
            if (!stripped.isEmpty()) {
                return stripped;
            }
        }

        // Try to find <Value> or subcodes as fallback if reason/text is empty
        String values = extractSoapFaultValues(xml);
        if (values != null) {
            return values;
        }

        // Try to find <message> as fallback
        String message = extractTagContent(xml, "message");
        if (message != null && !message.trim().isEmpty()) {
            return message.trim();
        }

        return "SOAP Fault: check device logs or raw response";
    }

    private String extractSoapFaultValues(String xml) {
        try {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    "<[\\w:\\-]*Value[^>]*>(.*?)</[\\w:\\-]*Value>",
                    java.util.regex.Pattern.CASE_INSENSITIVE
            );
            java.util.regex.Matcher matcher = pattern.matcher(xml);
            List<String> values = new ArrayList<>();
            while (matcher.find()) {
                String val = matcher.group(1).trim();
                if (!val.isEmpty() && !val.contains("Receiver") && !val.contains("Sender")) {
                    values.add(val);
                }
            }
            if (!values.isEmpty()) {
                return "SOAP Fault: " + String.join(", ", values);
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    private String extractTagContent(String xml, String tagName) {
        try {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    "<[\\w:\\-]*" + tagName + "[^>]*>(.*?)</[\\w:\\-]*" + tagName + ">",
                    java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL
            );
            java.util.regex.Matcher matcher = pattern.matcher(xml);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            // Ignore regex exceptions
        }
        return null;
    }

    private String sanitizeXml(String xml) {
        if (xml == null) {
            return null;
        }
        String sanitized = xml;
        sanitized = sanitized.replaceAll("(<(?:[\\w\\-]*:)?Password\\b[^>]*>)([^<]*)(</(?:[\\w\\-]*:)?Password>)", "$1***$3");
        sanitized = sanitized.replaceAll("(<(?:[\\w\\-]*:)?Nonce\\b[^>]*>)([^<]*)(</(?:[\\w\\-]*:)?Nonce>)", "$1***$3");
        return sanitized;
    }

}

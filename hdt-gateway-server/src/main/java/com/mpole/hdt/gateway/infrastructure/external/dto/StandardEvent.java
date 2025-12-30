package com.mpole.hdt.gateway.infrastructure.external.dto;

public class StandardEvent {
    private String source;     // vendor name
    private String type;       // event type
    private String deviceId;   // external device id
    private long timestampMs;  // epoch ms
    private String payloadJson; // raw payload (string)

    public StandardEvent() {}

    public StandardEvent(String source, String type, String deviceId, long timestampMs, String payloadJson) {
        this.source = source;
        this.type = type;
        this.deviceId = deviceId;
        this.timestampMs = timestampMs;
        this.payloadJson = payloadJson;
    }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public long getTimestampMs() { return timestampMs; }
    public void setTimestampMs(long timestampMs) { this.timestampMs = timestampMs; }
    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
}

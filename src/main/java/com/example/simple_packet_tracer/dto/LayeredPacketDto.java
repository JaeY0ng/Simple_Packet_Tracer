package com.example.simple_packet_tracer.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class LayeredPacketDto {
    private String timestamp;
    private Map<String, Map<String, Object>> layers = new LinkedHashMap<>();

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Map<String, Map<String, Object>> getLayers() {
        return layers;
    }

    public void addLayer(String layerName, Map<String, Object> fields) {
        this.layers.put(layerName, fields);
    }
}

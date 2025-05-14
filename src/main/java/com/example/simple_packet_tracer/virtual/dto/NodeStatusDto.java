package com.example.simple_packet_tracer.virtual.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NodeStatusDto {
    private String nodeId;
    private boolean isActive;
    private int connectionCount;
}

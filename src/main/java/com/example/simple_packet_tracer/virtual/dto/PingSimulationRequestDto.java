package com.example.simple_packet_tracer.virtual.dto;


import lombok.Data;

@Data
public class PingSimulationRequestDto {
    private String sourceNodeId;
    private String targetNodeId;
}

package com.example.simple_packet_tracer.dto;


import lombok.Data;

@Data
public class PingSimulationRequestDto {
    private String sourceNodeId;
    private String targetNodeId;
}

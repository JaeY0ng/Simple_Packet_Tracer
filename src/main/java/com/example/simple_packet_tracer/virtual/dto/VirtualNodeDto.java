package com.example.simple_packet_tracer.virtual.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualNodeDto {
    private String id;
    private String name;
//    private String type; // PC, ROUTER
    private String ipAddress;
//    private String macAddress;
//    private String gateway;
}

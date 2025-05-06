package com.example.simple_packet_tracer.virtual;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VirtualNode {
    private String id;
    private String name;
//    private String type; // PC, ROUTER
    private String ipAddress;
//    private String macAddress;
//    private String gateway;
}

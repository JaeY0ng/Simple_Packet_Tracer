package com.example.simple_packet_tracer.dto;

import lombok.Data;

@Data
public class PacketInfoDto {
    private String timestamp;
    private String srcIp;
    private String dstIp;
    private Integer srcPort;
    private Integer dstPort;
    private String protocol;
    private Integer length;
}

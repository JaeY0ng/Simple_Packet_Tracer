package com.example.simple_packet_tracer.virtual.dto;


import lombok.Data;

@Data
public class VirtualLinkDto {
    private String id;
    private String nodeAId;
    private String nodeBId;

    public VirtualLinkDto(String id, String nodeAId, String nodeBId) {
        this.id = id;
        this.nodeAId = nodeAId;
        this.nodeBId = nodeBId;
    }
}
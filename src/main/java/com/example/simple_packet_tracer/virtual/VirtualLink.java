package com.example.simple_packet_tracer.virtual;


import lombok.Data;

@Data
public class VirtualLink {
    private String id;
    private String nodeAId;
    private String nodeBId;

    public VirtualLink(String id, String nodeAId, String nodeBId) {
        this.id = id;
        this.nodeAId = nodeAId;
        this.nodeBId = nodeBId;
    }
}
package com.example.simple_packet_tracer.virtual.dto;

import lombok.Data;

@Data
public class CreateLinkRequestDto {
    private String nodeAId;
    private String nodeBId;
}
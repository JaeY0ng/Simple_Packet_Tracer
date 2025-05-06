package com.example.simple_packet_tracer.dto;

import lombok.Data;

@Data
public class FilterRequest {
    private String interfaceName;
    private String bpfFilter;
}


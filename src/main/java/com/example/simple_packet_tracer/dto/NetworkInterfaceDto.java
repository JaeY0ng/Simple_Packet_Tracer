package com.example.simple_packet_tracer.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetworkInterfaceDto {
    private String name;
    private String description;
    private boolean isLoopback;
    private List<String> ipAddresses;
}

package com.example.simple_packet_tracer.virtual.controller;


import com.example.simple_packet_tracer.virtual.dto.PingSimulationRequestDto;
import com.example.simple_packet_tracer.virtual.service.VirtualNetworkService;
import com.example.simple_packet_tracer.virtual.service.VirtualPingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/simulate")
@RequiredArgsConstructor
public class PingSimulationController {

    private final VirtualPingService pingService;

    @PostMapping("/ping")
    public ResponseEntity<?> simulatePing(@RequestBody PingSimulationRequestDto requestDto) {
        try{
            return ResponseEntity.ok(pingService.simulatePing(requestDto));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

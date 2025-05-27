package com.example.simple_packet_tracer.controller;


import com.example.simple_packet_tracer.dto.PingSimulationRequestDto;
import com.example.simple_packet_tracer.virtual.service.VirtualNetworkService;
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

    private final VirtualNetworkService virtualNetworkService;

    @PostMapping("/ping")
    public ResponseEntity<?> simulatePing(@RequestBody PingSimulationRequestDto requestDto) {
        String sourceNodeId = requestDto.getSourceNodeId();
        String targetNodeId = requestDto.getTargetNodeId();

        if(!virtualNetworkService.getNode(sourceNodeId).isPresent()||!virtualNetworkService.getNode(targetNodeId).isPresent()){
            return ResponseEntity.badRequest().body("존재하지 않는 노드 입니다");
        }

        boolean connected = virtualNetworkService
                .getAllLinks().stream()
                .anyMatch(link ->
                                (link.getNodeAId().equals(sourceNodeId) && link.getNodeBId().equals(targetNodeId)) || (link.getNodeAId().equals(targetNodeId) && link.getNodeBId().equals(sourceNodeId))
                        );
        return ResponseEntity.ok(Map.of(
                "source", sourceNodeId,
                "target" , targetNodeId,
                "reachable", connected
        ));
    }
}

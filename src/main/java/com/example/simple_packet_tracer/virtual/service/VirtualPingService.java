package com.example.simple_packet_tracer.virtual.service;

import com.example.simple_packet_tracer.virtual.dto.PingSimulationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VirtualPingService {
    private final VirtualNetworkService virtualNetworkService;

    public Map<String, Object> simulatePing(PingSimulationRequestDto requestDto) {
        String sourceNodeId = requestDto.getSourceNodeId();
        String targetNodeId = requestDto.getTargetNodeId();

        if(!virtualNetworkService.getNode(sourceNodeId).isPresent() || !virtualNetworkService.getNode(targetNodeId).isPresent()){
            throw new IllegalArgumentException("존재하지 않는 노드 입니다.");
        }

        boolean connected = virtualNetworkService.getAllLinks().stream()
                .anyMatch(link ->
                                (link.getNodeAId().equals(sourceNodeId) && link.getNodeBId().equals(targetNodeId)) ||
                                        (link.getNodeAId().equals(targetNodeId) && link.getNodeBId().equals(sourceNodeId))
                        );

        Map<String, Object> result = new HashMap<>();
        result.put("source", sourceNodeId);
        result.put("target", targetNodeId);
        result.put("reachable", connected);

        return result;
    }

}

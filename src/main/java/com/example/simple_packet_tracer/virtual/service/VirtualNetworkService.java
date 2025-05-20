package com.example.simple_packet_tracer.virtual.service;

import com.example.simple_packet_tracer.virtual.VirtualLink;
import com.example.simple_packet_tracer.virtual.VirtualNode;
import com.example.simple_packet_tracer.virtual.dto.NodeStatusDto;
import com.example.simple_packet_tracer.websocket.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class VirtualNetworkService {


    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, VirtualNode> nodeMap = new HashMap<>();
    private final List<VirtualLink> links = new ArrayList<>();

    public List<VirtualNode> getAllNodes() {
        return new ArrayList<>(nodeMap.values());
    }

    public VirtualNode addNode(VirtualNode node) {
        String nodeId = UUID.randomUUID().toString();
        node.setId(nodeId);
        nodeMap.put(nodeId, node);

        broadcastNodeStatus();
        return node;
    }

    public boolean deleteNode(String nodeId) {
        boolean removed = nodeMap.remove(nodeId) != null;
        if (removed) {
            links.removeIf(link -> link.getNodeAId().equals(nodeId) || link.getNodeBId().equals(nodeId));
            broadcastNodeStatus();
        }
        return removed;
    }

    public Optional<VirtualNode> getNode(String nodeId) {
        return Optional.ofNullable(nodeMap.get(nodeId));
    }

    public void clearAllNodes() {
        nodeMap.clear();
        links.clear();
    }

    public VirtualLink createLink(String nodeAId, String nodeBId) {
        if (!nodeMap.containsKey(nodeAId) || !nodeMap.containsKey(nodeBId)) {
            throw new IllegalArgumentException("노드 ID가 유효하지 않습니다.");
        }
        VirtualLink link = new VirtualLink(UUID.randomUUID().toString(), nodeAId, nodeBId);
        links.add(link);

        broadcastNodeStatus();
        return link;
    }

    public List<VirtualLink> getAllLinks() {
        return new ArrayList<>(links);
    }

    public boolean deleteLink(String linkId) {
        boolean removed = links.removeIf(link -> link.getId().equals(linkId));
        if (removed) {
            broadcastNodeStatus();
        }
        return removed;
    }

    public List<VirtualLink> getLinksByNode(String nodeId) {
        List<VirtualLink> result = new ArrayList<>();
        for (VirtualLink link : links) {
            if (link.getNodeAId().equals(nodeId) || link.getNodeBId().equals(nodeId)) {
                result.add(link);
            }
        }
        return result;
    }


    // 노드 상태 리스트 생성
    public List<NodeStatusDto> getNodeStatusList() {
        return nodeMap.values().stream()
                .map(node -> {
                    String nodeId = node.getId();
                    int connectionCount = countLinksForNode(nodeId);

                    return new NodeStatusDto(nodeId, true, connectionCount); // isActive = true 고정
                })
                .collect(Collectors.toList());
    }

    // 연결 수 계산
    private int countLinksForNode(String nodeId) {
        return (int) links.stream()
                .filter(link -> link.getNodeAId().equals(nodeId) || link.getNodeBId().equals(nodeId))
                .count();
    }

    public void broadcastNodeStatus (){
        List<NodeStatusDto> statusDtoList = getNodeStatusList();
        WebSocketMessage<List<NodeStatusDto>> message = new WebSocketMessage<>("NODE_STATUS_UPDATED",statusDtoList);

        messagingTemplate.convertAndSend("/topic/nodes/status", message);
    }
}

package com.example.simple_packet_tracer.virtual;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VirtualNetworkService {

    private final Map<String, VirtualNode> nodeMap = new HashMap<>();
    private final List<VirtualLink> links = new ArrayList<>();

    public List<VirtualNode> getAllNodes() {
        return new ArrayList<>(nodeMap.values());
    }

    public VirtualNode addNode(VirtualNode node) {
        String nodeId = UUID.randomUUID().toString();
        node.setId(nodeId);
        nodeMap.put(nodeId, node);
        return node;
    }

    public boolean deleteNode(String nodeId) {
        boolean removed = nodeMap.remove(nodeId) != null;
        if (removed) {
            links.removeIf(link -> link.getNodeAId().equals(nodeId) || link.getNodeBId().equals(nodeId));
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
        return link;
    }

    public List<VirtualLink> getAllLinks() {
        return new ArrayList<>(links);
    }

    public boolean deleteLink(String linkId) {
        return links.removeIf(link -> link.getId().equals(linkId));
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
}

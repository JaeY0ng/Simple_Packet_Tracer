package com.example.simple_packet_tracer.virtual;

import com.example.simple_packet_tracer.virtual.dto.CreateLinkRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/virtual")
public class VirtualNodeController {

    private final VirtualNetworkService networkService;
    private final VirtualNetworkService virtualNetworkService;

    public VirtualNodeController(VirtualNetworkService networkService, VirtualNetworkService virtualNetworkService) {
        this.networkService = networkService;
        this.virtualNetworkService = virtualNetworkService;
    }

    // 모든 노드 조회
    @GetMapping("/nodes")
    public List<VirtualNode> getAllNodes() {
        return networkService.getAllNodes();
    }

    // 노드 추가
    @PostMapping("/nodes")
    public ResponseEntity<VirtualNode> addNode(@RequestBody VirtualNode node) {
        return ResponseEntity.ok(networkService.addNode(node));
    }

    // 단일 노드 조회
    @GetMapping("/nodes/{id}")
    public ResponseEntity<VirtualNode> getNode(@PathVariable String id) {
        Optional<VirtualNode> node = networkService.getNode(id);
        return node.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 노드 삭제
    @DeleteMapping("/nodes/{id}")
    public ResponseEntity<Void> deleteNode(@PathVariable String id) {
        boolean removed = networkService.deleteNode(id);
        return removed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // 링크 생성
    @PostMapping("/links")
    public ResponseEntity<VirtualLink> createLink(@RequestBody CreateLinkRequestDto request) {
        VirtualLink link = virtualNetworkService.createLink(request.getNodeAId(), request.getNodeBId());
        return new ResponseEntity<>(link, HttpStatus.CREATED);
    }

    // 모든 링크 조회
    @GetMapping("/links")
    public List<VirtualLink> getAllLinks() {
        return networkService.getAllLinks();
    }

    // 노드와 연결된 링크 조회
    @GetMapping("/nodes/{id}/links")
    public List<VirtualLink> getLinksByNode(@PathVariable String id) {
        return networkService.getLinksByNode(id);
    }

    // 링크 삭제
    @DeleteMapping("/links/{id}")
    public ResponseEntity<Void> deleteLink(@PathVariable String id) {
        boolean removed = networkService.deleteLink(id);
        return removed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // 전체 초기화
    @DeleteMapping("/reset")
    public ResponseEntity<Void> clearAll() {
        networkService.clearAllNodes();
        return ResponseEntity.ok().build();
    }
}

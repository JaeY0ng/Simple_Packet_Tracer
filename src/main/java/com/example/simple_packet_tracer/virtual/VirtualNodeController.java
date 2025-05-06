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
    public ResponseEntity<?> getAllNodes() {
        List<VirtualNode> nodes = virtualNetworkService.getAllNodes();
        if(nodes.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message","노드가 존재하지 않습니다."));
        }

        return ResponseEntity.ok(nodes);
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
    public ResponseEntity<?> deleteNode(@PathVariable String id) {
        boolean removed = networkService.deleteNode(id);
        if (removed) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message","노드 삭제가 완료 되었습니다"));
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message","존재 하지 않는 노드 입니다."));
        }
    }

    // 링크 생성
    @PostMapping("/links")
    public ResponseEntity<VirtualLink> createLink(@RequestBody CreateLinkRequestDto request) {
        VirtualLink link = virtualNetworkService.createLink(request.getNodeAId(), request.getNodeBId());
        return new ResponseEntity<>(link, HttpStatus.CREATED);
    }

    // 모든 링크 조회
    @GetMapping("/links")
    public ResponseEntity<?> getAllLinks() {
        List<VirtualLink> links = virtualNetworkService.getAllLinks();
        if(links.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message","링크가 존재하지 않습니다."));
        }
        return ResponseEntity.ok(links);
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
    public ResponseEntity<?> clearAll() {
        networkService.clearAllNodes();
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message","가상 네트워크 초기화 작업이 완료 되었습니다."));
    }
}

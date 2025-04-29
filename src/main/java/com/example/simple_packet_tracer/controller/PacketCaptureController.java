package com.example.simple_packet_tracer.controller;


import com.example.simple_packet_tracer.service.PacketCaptureService;
import org.pcap4j.packet.Packet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PacketCaptureController {

    private final PacketCaptureService packetCaptureService;

    public PacketCaptureController(PacketCaptureService packetCaptureService) {
        this.packetCaptureService = packetCaptureService;
    }

    @GetMapping("/interface")
    public String listInterfaces() throws Exception{
        packetCaptureService.listNetworkInterFace();
        return "네트워크 인터페이스 조회 완료";
    }

    @GetMapping("/capture")
    public String capturePackets() throws Exception{
        packetCaptureService.capturePackets();
        return "패킷 캡처 완료";
    }
}

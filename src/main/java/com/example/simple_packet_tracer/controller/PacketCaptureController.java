package com.example.simple_packet_tracer.controller;


import com.example.simple_packet_tracer.service.PacketCaptureService;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.packet.Packet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PacketCaptureController {

    private final PacketCaptureService packetCaptureService;

    public PacketCaptureController(PacketCaptureService packetCaptureService) {
        this.packetCaptureService = packetCaptureService;
    }

    @GetMapping("/interface")
    public List<String> listInterfaces() throws Exception{
        List<PcapNetworkInterface> interfaces = packetCaptureService.listNetworkInterfaces();
        return interfaces.stream()
                .map(nif -> nif.getName() + " : " + nif.getDescription())
                .collect(Collectors.toList());
    }

    @GetMapping("/capture")
    public String capturePackets(
            @RequestParam String interfaceName,
            @RequestParam(required = false) String bpfFilter
    ) throws PcapNativeException, NotOpenException {
        packetCaptureService.capturePackets(interfaceName, bpfFilter);
        return "패킷 캡처 완료";
    }
}

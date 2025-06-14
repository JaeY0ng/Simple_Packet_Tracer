package com.example.simple_packet_tracer.controller;


import com.example.simple_packet_tracer.dto.LayeredPacketDto;
import com.example.simple_packet_tracer.dto.NetworkInterfaceDto;
import com.example.simple_packet_tracer.service.PacketCaptureService;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet4Address;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PacketCaptureController {

    private final PacketCaptureService packetCaptureService;

    public PacketCaptureController(PacketCaptureService packetCaptureService) {
        this.packetCaptureService = packetCaptureService;
    }

    @GetMapping("/interface")
    public List<NetworkInterfaceDto> listInterfaces() throws Exception {
        List<PcapNetworkInterface> devs = Pcaps.findAllDevs();

        for (PcapNetworkInterface nif : devs) {
            System.out.println("Interface: " + nif.getName());
            System.out.println("Description: " + (nif.getDescription() != null ? nif.getDescription() : "No description"));
            System.out.println("Loopback: " + nif.isLoopBack());
            System.out.println("Addresses:");
            for (PcapAddress addr : nif.getAddresses()) {
                if (addr.getAddress() != null) {
                    System.out.println("    - " + addr.getAddress().getHostAddress());
                }
            }
            System.out.println("---------------");
        }

        return devs.stream()
                .filter(nif -> nif.getAddresses().stream().anyMatch(addr ->
                        addr.getAddress() != null &&
                                addr.getAddress() instanceof Inet4Address &&
                                !addr.getAddress().isLoopbackAddress()
                ))
                .map(nif -> new NetworkInterfaceDto(
                        nif.getName(),
                        nif.getDescription() != null ? nif.getDescription() : "No description",
                        nif.isLoopBack(),
                        nif.getAddresses().stream()
                                .map(addr -> addr.getAddress().getHostAddress())
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }


    @GetMapping("/capture")
    public List<LayeredPacketDto> capturePackets(
            @RequestParam String interfaceName,
            @RequestParam(required = false) String bpfFilter
    ) throws PcapNativeException, NotOpenException, InterruptedException {
        // 필터 디코딩
        String decodedFilter = bpfFilter != null ? URLDecoder.decode(bpfFilter, StandardCharsets.UTF_8) : null;

        System.out.println("🎯 클라이언트 필터 값: " + decodedFilter); // 디버그용
        return packetCaptureService.capturePackets(interfaceName, decodedFilter);
    }
}

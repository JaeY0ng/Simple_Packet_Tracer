package com.example.simple_packet_tracer.service;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacketCaptureService {

    private static final int SNAPLEN = 65536; // 최대 패킷 크기
    private static final int READ_TIMEOUT = 50; // 밀리초 단위
    private static final int MAX_PACKET_COUNT = 10; // 캡처할 패킷 수


    public List<PcapNetworkInterface> listNetworkInterfaces() throws PcapNativeException{
        return Pcaps.findAllDevs();
    }

    public void capturePackets(String interfaceName, String bpfFilter) throws PcapNativeException, NotOpenException {
        List<PcapNetworkInterface> interfaces = Pcaps.findAllDevs();
        if (interfaces == null || interfaces.isEmpty()) {
            System.out.println("네트워크 인터페이스를 찾을 수 없습니다.");
            return;
        }

        PcapNetworkInterface selectedNif = interfaces.stream()
                .filter(nif -> nif.getName().equals(interfaceName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("인터페이스를 찾을 수 없습니다: " + interfaceName));

        System.out.println("선택된 인터페이스로 캡처 중: " + selectedNif.getName());

        PcapHandle handle = selectedNif.openLive(
                SNAPLEN,
                PcapNetworkInterface.PromiscuousMode.PROMISCUOUS,
                READ_TIMEOUT
        );

        if (bpfFilter != null && !bpfFilter.isEmpty()) {
            handle.setFilter(bpfFilter, BpfProgram.BpfCompileMode.OPTIMIZE);
            System.out.println("BPF 필터 적용됨: " + bpfFilter);
        }

        int packetCount = 0;
        while (packetCount < MAX_PACKET_COUNT) {
            Packet packet = handle.getNextPacket();
            if (packet != null) {
                System.out.println("캡처된 패킷: " + packet);
                packetCount++;
            }
        }

        handle.close();
    }
}
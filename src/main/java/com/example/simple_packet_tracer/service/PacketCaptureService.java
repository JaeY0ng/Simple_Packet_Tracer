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

    public void listNetworkInterFace() throws PcapNativeException{
        List<PcapNetworkInterface> interfaces = Pcaps.findAllDevs();
        if (interfaces == null || interfaces.isEmpty()) {
            System.out.println("네트워크 인터페이스를 찾을 수 없음");
            return;
        }

        System.out.println("네트워크 인터페이스 : ");
        for (PcapNetworkInterface nif : interfaces) {
            System.out.println(nif.getName() + " : " + nif.getDescription());
        }
    }

    public void capturePackets() throws PcapNativeException, NotOpenException{
        List<PcapNetworkInterface> interfaces = Pcaps.findAllDevs();
        if (interfaces == null || interfaces.isEmpty()) {
            System.out.println("네트워크를 찾을 수 없습니다.");
            return;
        }

        // 첫 번째 네트워크 인터페이스 사용
        PcapNetworkInterface nif = interfaces.get(0);
        System.out.println("인터페이스 캡쳐 : " + nif.getName());

        PcapHandle handle = nif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);

        int packetCount = 0;
        while (packetCount < MAX_PACKET_COUNT) {
            Packet packet = handle.getNextPacket();
            if (packet != null) {
                System.out.println("Packet captured : " + packet);
                packetCount++;
            }
        }

        handle.close();
    }
}

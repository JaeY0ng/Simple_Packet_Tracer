package com.example.simple_packet_tracer.service;

import com.example.simple_packet_tracer.dto.LayeredPacketDto;
import com.example.simple_packet_tracer.websocket.PacketWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class PacketCaptureService {

    private static final int SNAPLEN = 65536; // 최대 패킷 크기
    private static final int READ_TIMEOUT = 100; // 밀리초 단위
    private static final int MAX_PACKET_COUNT = 50; // 캡처할 패킷 수
    private static final int TIMEOUT_SECONDS = 7; // 최대 대기 시간

    private final PacketWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<PcapNetworkInterface> listNetworkInterfaces() throws PcapNativeException {
        return Pcaps.findAllDevs();
    }

    public PacketCaptureService(PacketWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    public List<LayeredPacketDto> capturePackets(String interfaceName, String bpfFilter) throws PcapNativeException, NotOpenException, InterruptedException {

        List<LayeredPacketDto> capturedPackets = new ArrayList<>();

        List<PcapNetworkInterface> interfaces = Pcaps.findAllDevs();

        String normalized = interfaceName.replaceAll("\\\\\\\\", "\\\\"); // \\ → \

        if (interfaces == null || interfaces.isEmpty()) {
            System.out.println("네트워크 인터페이스를 찾을 수 없습니다.");
        }

        PcapNetworkInterface selectedNif = interfaces.stream().filter(nif -> nif.getName().equals(normalized)).findFirst().orElseThrow(() -> new IllegalArgumentException("인터페이스를 찾을 수 없습니다: " + interfaceName));

        System.out.println("선택된 인터페이스로 캡처 중: " + selectedNif.getName());

        PcapHandle handle = selectedNif.openLive(SNAPLEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);

        if (bpfFilter != null && !bpfFilter.isEmpty()) {
            handle.setFilter(bpfFilter, BpfProgram.BpfCompileMode.OPTIMIZE);
            System.out.println("BPF 필터 적용됨: " + bpfFilter);
        }

        PacketListener listener = packet -> {
            LayeredPacketDto dto = parsePacket(packet, Instant.now());
            if (dto != null) {
                System.out.println("캡처된 패킷 : " + packet);
                try {
                    String json = objectMapper.writeValueAsString(dto);
                    webSocketHandler.sendPacketMessage(json); // 💥 WebSocket 전송
                } catch (Exception e) {
                    e.printStackTrace();
                }

                synchronized (capturedPackets) {
                    capturedPackets.add(dto);
                }
            }
        };

        // 별도 쓰레드에서 loop 실행
        ExecutorService captureExecutor = Executors.newSingleThreadExecutor();
        Future<?> future = captureExecutor.submit(() -> {
            try {
                handle.loop(MAX_PACKET_COUNT, listener);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                handle.close();
            }
        });

        // 최대 TIMEOUT_SECONDS 초만 기다림
        try {
            future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS); // 타임아웃 시 InterruptedException
        } catch (TimeoutException | ExecutionException e) {
            handle.breakLoop(); // 강제 종료
            System.out.println("타임아웃 도달, loop 중단");
        } finally {
            captureExecutor.shutdown();
        }

        return capturedPackets;
    }

    private LayeredPacketDto parsePacket(Packet packet, Instant timestamp) {
        LayeredPacketDto dto = new LayeredPacketDto();
        dto.setTimestamp(timestamp.toString());

        // Ethernet
        EthernetPacket eth = packet.get(EthernetPacket.class);
        if (eth != null) {
            Map<String, Object> ethFields = new HashMap<>();
            ethFields.put("srcMac", eth.getHeader().getSrcAddr().toString());
            ethFields.put("dstMac", eth.getHeader().getDstAddr().toString());
            ethFields.put("type", eth.getHeader().getType().toString());
            dto.addLayer("Ethernet", ethFields);
        }

        // IPv4
        IpV4Packet ip = packet.get(IpV4Packet.class);
        if (ip != null) {
            Map<String, Object> ipFields = new HashMap<>();
            ipFields.put("srcIp", ip.getHeader().getSrcAddr().getHostAddress());
            ipFields.put("dstIp", ip.getHeader().getDstAddr().getHostAddress());
            ipFields.put("protocol", ip.getHeader().getProtocol().name());
            dto.addLayer("IPv4", ipFields);
        }

        // TCP
        TcpPacket tcp = packet.get(TcpPacket.class);
        if (tcp != null) {
            Map<String, Object> tcpFields = new HashMap<>();
            tcpFields.put("srcPort", tcp.getHeader().getSrcPort().valueAsInt());
            tcpFields.put("dstPort", tcp.getHeader().getDstPort().valueAsInt());
            dto.addLayer("TCP", tcpFields);
        }

        // UDP
        UdpPacket udp = packet.get(UdpPacket.class);
        if (udp != null) {
            Map<String, Object> udpFields = new HashMap<>();
            udpFields.put("srcPort", udp.getHeader().getSrcPort().valueAsInt());
            udpFields.put("dstPort", udp.getHeader().getDstPort().valueAsInt());
            dto.addLayer("UDP", udpFields);
        }

        // DNS 분석
        if (udp != null && (udp.getHeader().getSrcPort().valueAsInt() == 53 || udp.getHeader().getDstPort().valueAsInt() == 53)) {

            byte[] payload = udp.getPayload().getRawData();
            if (payload.length > 12) { // 최소 DNS 헤더는 12바이트
                Map<String, Object> dnsFields = new HashMap<>();

                int qdCount = ((payload[4] & 0xFF) << 8) | (payload[5] & 0xFF); // 질의 개수
                dnsFields.put("questions", qdCount);

                // 간단하게 쿼리 이름 추출 시도
                StringBuilder queryName = new StringBuilder();
                int idx = 12; // 쿼리 이름 시작 위치
                while (idx < payload.length && payload[idx] != 0) {
                    int len = payload[idx++] & 0xFF;
                    if (len + idx > payload.length) break;
                    queryName.append(new String(payload, idx, len)).append(".");
                    idx += len;
                }
                if (!queryName.isEmpty()) {
                    dnsFields.put("query", queryName.toString());
                }

                dto.addLayer("DNS", dnsFields);
            }
        }

        // HTTP 분석 (단순 헤더 디코딩)
        if (tcp != null && (tcp.getHeader().getSrcPort().valueAsInt() == 80 || tcp.getHeader().getDstPort().valueAsInt() == 80)) {

            byte[] payload = tcp.getPayload() != null ? tcp.getPayload().getRawData() : null;
            if (payload != null && payload.length > 0) {
                String httpPayload = new String(payload);
                Map<String, Object> httpFields = new HashMap<>();

                // 요청인지 응답인지 구분
                if (httpPayload.startsWith("GET") || httpPayload.startsWith("POST")) {
                    httpFields.put("type", "Request");
                } else if (httpPayload.startsWith("HTTP/")) {
                    httpFields.put("type", "Response");
                }

                // 첫 줄만 가져오기 (요청 라인 또는 상태 라인)
                int endOfLine = httpPayload.indexOf("\r\n");
                if (endOfLine != -1) {
                    httpFields.put("startLine", httpPayload.substring(0, endOfLine));
                }

                dto.addLayer("HTTP", httpFields);
            }
        }

        return dto;
    }
}
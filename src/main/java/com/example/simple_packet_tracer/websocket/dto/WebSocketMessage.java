package com.example.simple_packet_tracer.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebSocketMessage<T> {

    // WebSocket 메세지 표준 구조 = "type" : "NODE_STATUS_UPDATE" , "data" : [...]

    private String type;
    private T data;

}

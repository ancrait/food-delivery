package com.sorokaandriy.delivery_service.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sorokaandriy.delivery_service.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryWebSocketHandler extends TextWebSocketHandler {

    private final DeliveryService deliveryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // to get session by riderId for writing from session to rider
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // add to map
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String riderId = getRiderId(session);
        if (riderId == null) {
            log.warn("WebSocket connection rejected: no riderId in session attributes");
            return;
        }
        sessions.put(riderId, session);
        log.info("Rider {} connected via WebSocket", riderId);
    }

    // for handle rider actions
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String riderId = getRiderId(session);
        if (riderId == null) {
            log.warn("Unauthorized WebSocket message: no riderId");
            return;
        }

        JsonNode json = objectMapper.readTree(message.getPayload());
        if (!json.has("event") || !json.has("deliveryId")) {
            log.warn("Invalid WebSocket message: missing event or deliveryId");
            return;
        }

        String event = json.get("event").asText();
        UUID deliveryId = UUID.fromString(json.get("deliveryId").asText());

        switch (event) {
            case "ACCEPT" -> deliveryService.acceptDelivery(deliveryId, riderId);
            case "DECLINE" -> deliveryService.declineDelivery(deliveryId, riderId);
            case "PICKED_UP" -> deliveryService.pickupDelivery(deliveryId, riderId);
            case "DELIVERED" -> deliveryService.completeDelivery(deliveryId, riderId);
            default -> log.warn("Unknown event: {}", event);
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String riderId = getRiderId(session);
        if (riderId != null) {
            sessions.remove(riderId);
            log.info("Rider {} disconnected", riderId);
        }
    }

    // for writing rider from service
    public void sendToRider(String riderId, String json) {
        WebSocketSession session = sessions.get(riderId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(json));
            } catch (Exception e) {
                log.error("Failed to send message to rider {}: {}", riderId, e.getMessage());
            }
        }
    }

    public boolean isRiderOnline(String riderId) {
        WebSocketSession session = sessions.get(riderId);
        return session != null && session.isOpen();
    }

    private String getRiderId(WebSocketSession session) {
        Object riderId = session.getAttributes().get("riderId");
        return riderId != null ? riderId.toString() : null;
    }
}

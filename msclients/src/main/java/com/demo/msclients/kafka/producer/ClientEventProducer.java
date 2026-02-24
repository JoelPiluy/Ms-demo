package com.demo.msclients.kafka.producer;


import com.demo.msclients.domain.entity.Client;
import com.demo.msclients.kafka.event.ClientCreatorEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class ClientEventProducer {
    private final KafkaTemplate<String, ClientCreatorEvent> kafkaTemplate;

    @Value("${kafka.topic.client-created}")
    private String topicClientCreated;

    public void publishClientCreated(Client cliente) {
        ClientCreatorEvent event = ClientCreatorEvent.builder()
                .id(cliente.getId())
                .clientId(cliente.getClientId())
                .name(cliente.getName())
                .identification(cliente.getIdentification())
                .status(cliente.getStatus())
                .build();

        log.info("Publicando evento ClienteCreado en topic '{}': {}", topicClientCreated, event);
        kafkaTemplate.send(topicClientCreated, cliente.getClientId(), event);
    }
}

package com.demo.msaccounts.kafka.consumer;

import com.demo.msaccounts.domain.entity.ClientRef;
import com.demo.msaccounts.kafka.event.ClientCreatedEvent;
import com.demo.msaccounts.repository.ClientRefRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientEventConsumer {

    private final ClientRefRepository clientRefRepository;

    @KafkaListener(
            topics = "${kafka.topic.client-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onClientCreated(ClientCreatedEvent event) {
        log.info("Received ClientCreated event: {}", event);

        // Idempotent — avoid duplicates if event is replayed
        if (clientRefRepository.existsByClientId(event.getClientId())) {
            log.warn("ClientRef already exists for clientId: {}. Skipping.", event.getClientId());
            return;
        }

        ClientRef clientRef = ClientRef.builder()
                .clientId(event.getClientId())
                .name(event.getName())
                .identification(event.getIdentification())
                .status(event.getStatus())
                .createdBy("kafka-event")
                .build();

        clientRefRepository.save(clientRef);
        log.info("ClientRef saved for clientId: {}", event.getClientId());
    }
}

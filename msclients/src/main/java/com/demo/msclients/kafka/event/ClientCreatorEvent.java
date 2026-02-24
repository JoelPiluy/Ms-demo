package com.demo.msclients.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreatorEvent {
    private Long id;
    private String clientId;
    private String name;
    private String identification;
    private Boolean status;
}

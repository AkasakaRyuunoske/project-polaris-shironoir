package projectpolaris.ProjectPolarisShironoir.Messaging;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class KafkaConfigs {
    // Default Configurations
    @Value("${polaris.kafka.default-partitions-count}")
    private int default_partitions_count;

    @Value("${polaris.kafka.default-replicas-count}")
    private int default_replicas_count;


    @Value("${polaris.kafka.groupId}")
    private String groupId;

    //Topic Names

    @Value("${polaris.kafka.topics.utsup}")
    private String utsup;

    @Value("${polaris.kafka.topics.statistics}")
    private String statistics;

    @Value("${polaris.kafka.topics.security}")
    private String security;

    @Value("${polaris.kafka.topics.data-layer}")
    private String dataLayer;

    @Value("${polaris.kafka.topics.front-end-gateway}")
    private String frontEndGateway;

    @Value("${polaris.kafka.topics.errors-security}")
    private String errorsSecurity;

    @Value("${polaris.kafka.topics.errors-REST}")
    private String errorsREST;

    @Value("${polaris.kafka.topics.errors-SOAP}")
    private String errorsSOAP;

    @Value("${polaris.kafka.topics.errors-internal}")
    private String errorsInternal;

}

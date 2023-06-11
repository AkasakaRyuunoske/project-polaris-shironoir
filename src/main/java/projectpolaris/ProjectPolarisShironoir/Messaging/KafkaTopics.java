package projectpolaris.ProjectPolarisShironoir.Messaging;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Log4j2
public class KafkaTopics {
    @Autowired
    KafkaConfigs kafkaConfigs;

    @Bean
    public NewTopic utsuP() {
        log.info("[Topic created]: " + kafkaConfigs.getUtsup());
        return TopicBuilder.name(kafkaConfigs.getUtsup())
                .partitions(kafkaConfigs.getDefault_partitions_count())
                .replicas(kafkaConfigs.getDefault_replicas_count())
                .build();
    }

    @Bean
    public NewTopic security() {
        log.info("[Topic created]: " + kafkaConfigs.getSecurity());
        return TopicBuilder.name(kafkaConfigs.getSecurity())
                .partitions(kafkaConfigs.getDefault_partitions_count())
                .replicas(kafkaConfigs.getDefault_replicas_count())
                .build();
    }

    @Bean
    public NewTopic statistics() {
        log.info("[Topic created]: " + kafkaConfigs.getStatistics());
        return TopicBuilder.name(kafkaConfigs.getStatistics()) // #todo maybe more topics, like statistics-front-end, statistics-SBES, statistics-MBE, statistics-SBEP, statistics-errors
                .partitions(kafkaConfigs.getDefault_partitions_count())
                .replicas(kafkaConfigs.getDefault_replicas_count())
                .build();
    }

    @Bean
    public NewTopic dataLayer() {
        log.info("[Topic created]: " + kafkaConfigs.getDataLayer());
        return TopicBuilder.name(kafkaConfigs.getDataLayer())
                .partitions(kafkaConfigs.getDefault_partitions_count())
                .replicas(kafkaConfigs.getDefault_replicas_count())
                .build();
    }

    @Bean
    public NewTopic frontEndGateway() {
        log.info("[Topic created]: " + kafkaConfigs.getFrontEndGateway());
        return TopicBuilder.name(kafkaConfigs.getFrontEndGateway())
                .partitions(kafkaConfigs.getDefault_partitions_count())
                .replicas(kafkaConfigs.getDefault_replicas_count())
                .build();
    }

    // Error dedicated Topics

    @Bean
    public NewTopic errorsSecurity() {
        log.info("[Topic created]: " + kafkaConfigs.getErrorsSecurity());
        return TopicBuilder.name(kafkaConfigs.getErrorsSecurity())
                .partitions(kafkaConfigs.getDefault_partitions_count())
                .replicas(kafkaConfigs.getDefault_replicas_count())
                .build();
    }

    @Bean
    public NewTopic errorsREST() {
        log.info("[Topic created]: " + kafkaConfigs.getErrorsREST());
        return TopicBuilder.name(kafkaConfigs.getErrorsREST())
                .partitions(kafkaConfigs.getDefault_partitions_count())
                .replicas(kafkaConfigs.getDefault_replicas_count())
                .build();
    }

    @Bean
    public NewTopic errorsSOAP() {
        log.info("[Topic created]: " + kafkaConfigs.getErrorsSOAP());
        return TopicBuilder.name(kafkaConfigs.getErrorsSOAP())
                .partitions(kafkaConfigs.getDefault_partitions_count())
                .replicas(kafkaConfigs.getDefault_replicas_count())
                .build();
    }

    @Bean
    public NewTopic errorsInternal() {
        log.info("[Topic created]: " + kafkaConfigs.getErrorsInternal());
        return TopicBuilder.name(kafkaConfigs.getErrorsInternal())
                .partitions(kafkaConfigs.getDefault_partitions_count())
                .replicas(kafkaConfigs.getDefault_replicas_count())
                .build();
    }
}

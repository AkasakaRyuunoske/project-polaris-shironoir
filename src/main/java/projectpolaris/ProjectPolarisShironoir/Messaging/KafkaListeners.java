package projectpolaris.ProjectPolarisShironoir.Messaging;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class KafkaListeners {
    @Autowired
    KafkaConfigs kafkaConfigs;

//    @KafkaListener(topics = "${polaris.kafka.topics.utsup:utsup}", groupId = "${polaris.kafka.groupId:ArcWarden}")
//    void listener_utsup(String data){
//        log.info("[LISTENING: UtsuP]: " + data);
//    }
//
//    @KafkaListener(topics = "${polaris.kafka.topics.statistics:statistics}", groupId = "${polaris.kafka.groupId:ArcWarden}")
//    void listener_statistics(String data){
//        log.info("[LISTENING: UtsuP]: " + data);
//    }
//
//
//    @KafkaListener(topics = "${polaris.kafka.topics.security:security}", groupId = "${polaris.kafka.groupId:ArcWarden}")
//    void listener_security(String data){
//        log.info("[LISTENING: security]: " + data);
//    }
//
//    @KafkaListener(topics = "${polaris.kafka.topics.data-layer:dataLayer}", groupId = "${polaris.kafka.groupId:ArcWarden}")
//    void listener_dataLayer(String data){
//        log.info("[LISTENING: dataLayer]: " + data);
//    }
//
//    @KafkaListener(topics = "${polaris.kafka.topics.front-end-gateway:frontEndGateway}", groupId = "${polaris.kafka.groupId:ArcWarden}")
//    void listener_front_end_gateway(String data){
//        log.info("[LISTENING: frontEndGateway]: " + data);
//    }
//
//    @KafkaListener(topics = "${polaris.kafka.topics.security:errorsSecurity}", groupId = "${polaris.kafka.groupId:ArcWarden}")
//    void listener_errors_security(String data){
//        log.info("[LISTENING: errorsSecurity]: " + data);
//    }
//
//    @KafkaListener(topics = "${polaris.kafka.topics.errors-REST:errorsREST}", groupId = "${polaris.kafka.groupId:ArcWarden}")
//    void listener_errors_REST(String data){
//        log.info("[LISTENING: errorsREST]: " + data);
//    }
//
//    @KafkaListener(topics = "${polaris.kafka.topics.errors-SOAP:errorsSOAP}", groupId = "${polaris.kafka.groupId:ArcWarden}")
//    void listener_errors_SOAP(String data){
//        log.info("[LISTENING: errorsSOAP]: " + data);
//    }
//
//    @KafkaListener(topics = "${polaris.kafka.topics.errors-internal:errorsInternal}", groupId = "${polaris.kafka.groupId:ArcWarden}")
//    void listener_errors_internal(String data){
//        log.info("[LISTENING: errorsInternal]: " + data);
//    }
}

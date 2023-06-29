package projectpolaris.ProjectPolarisShironoir.Handshake.SecurityServices;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import projectpolaris.ProjectPolarisShironoir.Messaging.KafkaConfigs;

import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
public class HandshakePostman {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    KafkaConfigs kafkaConfigs;

    public Map<String, String> contact(String uri, String message_out){

        Map<String, String> contact_result = new HashMap<>();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> httpEntity = new HttpEntity<>(message_out, headers);

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.postForObject(uri, httpEntity, String.class);

            contact_result.put("Response Message", result);

            log.info("result: " + result);

        } catch (HttpClientErrorException httpClientErrorException){
            log.error("Error occurred on Client side. Error message: " + httpClientErrorException);
            kafkaTemplate.send(kafkaConfigs.getErrorsSecurity(), "[HANDSHAKE CONTROLLER] Client side error occurred....");

            if (httpClientErrorException.getStatusCode().toString().equals("400 BAD_REQUEST")){ //#todo must be handled differently, for now it is only placeholder
                log.error("More precisely an " + httpClientErrorException.getStatusCode() + " occurred");
                kafkaTemplate.send(kafkaConfigs.getErrorsSecurity(), "[HANDSHAKE CONTROLLER] More precisely an " + httpClientErrorException.getStatusCode() + " occurred");

                contact_result.put("Body", "<h1>Error occurred on Client side:"
                        + httpClientErrorException.getStatusCode()
                        + "</h1>"
                        + "<br>"
                        + "<h1> Error message:"
                        + httpClientErrorException.getResponseBodyAsString()
                        + "</h1>");
                contact_result.put("Status", httpClientErrorException.getStatusCode().toString());

                return contact_result;
            }

        } catch (HttpServerErrorException httpServerErrorException) {
            log.error("Error occurred on Server side. Error message: " + httpServerErrorException);
            kafkaTemplate.send(kafkaConfigs.getErrorsSecurity(), "[HANDSHAKE CONTROLLER] Server side error occurred....");

            if (httpServerErrorException.getStatusCode().toString().equals("500 INTERNAL_SERVER_ERROR")){ //#todo must be handled differently, for now it is only placeholder
                log.error("More precisely an " + httpServerErrorException.getStatusCode() + " occurred");
                kafkaTemplate.send(kafkaConfigs.getErrorsSecurity(), "[HANDSHAKE CONTROLLER] More precisely an " + httpServerErrorException.getStatusCode() + " occurred");

                contact_result.put("Body", "<h1>Error occurred on Server side:"
                        + httpServerErrorException.getStatusCode()
                        + "</h1>"
                        + "<br>"
                        + "<h1> Error message:"
                        + httpServerErrorException.getResponseBodyAsString()
                        + "</h1>");
                contact_result.put("Status", httpServerErrorException.getStatusCode().toString());

                return contact_result;
            }
        }

        return contact_result;
    }

    public Map<String, String> contact(String uri, Map<String, String> message_out){

        Map<String, String> contact_result = new HashMap<>();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(message_out, headers);

            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> result = restTemplate.postForObject(uri, httpEntity, Map.class);

            contact_result = result;

            log.info("result: " + result);

        } catch (HttpClientErrorException httpClientErrorException){
            log.error("Error occurred on Client side. Error message: " + httpClientErrorException);
            kafkaTemplate.send(kafkaConfigs.getErrorsSecurity(), "[HANDSHAKE CONTROLLER] Client side error occurred....");

            if (httpClientErrorException.getStatusCode().toString().equals("400 BAD_REQUEST")){ //#todo must be handled differently, for now it is only placeholder
                log.error("More precisely an " + httpClientErrorException.getStatusCode() + " occurred");
                kafkaTemplate.send(kafkaConfigs.getErrorsSecurity(), "[HANDSHAKE CONTROLLER] More precisely an " + httpClientErrorException.getStatusCode() + " occurred");

                contact_result.put("Body", "<h1>Error occurred on Client side:"
                        + httpClientErrorException.getStatusCode()
                        + "</h1>"
                        + "<br>"
                        + "<h1> Error message:"
                        + httpClientErrorException.getResponseBodyAsString()
                        + "</h1>");
                contact_result.put("Status", httpClientErrorException.getStatusCode().toString());

                return contact_result;
            }

        } catch (HttpServerErrorException httpServerErrorException) {
            log.error("Error occurred on Server side. Error message: " + httpServerErrorException);
            kafkaTemplate.send(kafkaConfigs.getErrorsSecurity(), "[HANDSHAKE CONTROLLER] Server side error occurred....");

            if (httpServerErrorException.getStatusCode().toString().equals("500 INTERNAL_SERVER_ERROR")){ //#todo must be handled differently, for now it is only placeholder
                log.error("More precisely an " + httpServerErrorException.getStatusCode() + " occurred");
                kafkaTemplate.send(kafkaConfigs.getErrorsSecurity(), "[HANDSHAKE CONTROLLER] More precisely an " + httpServerErrorException.getStatusCode() + " occurred");

                contact_result.put("Body", "<h1>Error occurred on Server side:"
                        + httpServerErrorException.getStatusCode()
                        + "</h1>"
                        + "<br>"
                        + "<h1> Error message:"
                        + httpServerErrorException.getResponseBodyAsString()
                        + "</h1>");
                contact_result.put("Status", httpServerErrorException.getStatusCode().toString());

                return contact_result;
            }
        }

        return contact_result;
    }
}

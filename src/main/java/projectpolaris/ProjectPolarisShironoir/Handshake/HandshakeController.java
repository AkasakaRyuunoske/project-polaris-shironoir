package projectpolaris.ProjectPolarisShironoir.Handshake;

import lombok.extern.log4j.Log4j2;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import projectpolaris.ProjectPolarisShironoir.Handshake.SecurityServices.HandshakePostman;
import projectpolaris.ProjectPolarisShironoir.Messaging.KafkaConfigs;
import projectpolaris.ProjectPolarisShironoir.POJOs.Anaata;
import projectpolaris.ProjectPolarisShironoir.Repository.AnaataRepository;

import java.security.cert.X509Certificate;
import java.util.*;

@Log4j2
@RestController
@RequestMapping("/handshake")
public class HandshakeController {
    @Autowired
    Camellia camellia;

    @Autowired
    CertificationGenerator certificationGenerator;

    @Autowired
    KafkaConfigs kafkaConfigs;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    HandshakePostman handshakePostman;

    @Autowired
    AnaataRepository anaataRepository;

    // Handshake process as INITIATOR
    @GetMapping("/start-handshake") //#todo for testing purposes is GET, must be changed to POST later
    public ResponseEntity<String> initiateHandshake() {
        kafkaTemplate.send(kafkaConfigs.getSecurity(), "[HANDSHAKE CONTROLLER] Handshake Requested....");
        log.info("Handshake started...");

        // #todo 1. add a way to know whom to call
        Map<String, String> result = handshakePostman.contact("http://localhost:8090/handshake/start-handshake", "Nee");

        if (result.get("Response Message") != null && result.get("Response Message").equals("Nee Nee")) {
            kafkaTemplate.send(kafkaConfigs.getSecurity(), "[HANDSHAKE CONTROLLER] Nee Nee received, handshake proceeds");
            log.info("Nee Nee");

            proceedHandshake_SendChest();
        }

        return new ResponseEntity<>(result.get("Body"), HttpStatus.ACCEPTED);
    }

    @PostMapping("/proceed-handshake/send_chest")
    private ResponseEntity<Map<String, String>> proceedHandshake_SendChest() {
        // Create a response map that will be used later
        Map<String, String> response = new HashMap<>();

        // Log about
        log.info("Nee Nee Neeee");
        kafkaTemplate.send(kafkaConfigs.getSecurity(), "[HANDSHAKE CONTROLLER] Sending Chest...");

        // Certification Generation
        X509Certificate certificate;

        certificate = certificationGenerator.generateCertification();

        String salt = BCrypt.gensalt();
        String hashedMessage = BCrypt.hashpw(certificate.getPublicKey().toString() + certificate, salt);

        // Creating and populating request's body
        Map<String, String> chest = new HashMap<>();

        chest.put("PK", certificate.getPublicKey().toString());
        chest.put("Certificate", certificate.toString());
        chest.put("HASH", hashedMessage);
        chest.put("salt", salt);

        // #todo 1. add a way to know whom to call
        Map<String, String> result_chest = handshakePostman.contact("http://localhost:8090/handshake/proceed-handshake/get_chest", chest);

        if (result_chest != null) {
            if (result_chest.containsKey("Certificate") && result_chest.containsKey("HASH") && result_chest.containsKey("salt") && result_chest.containsKey("PK: IV") && result_chest.containsKey("PK: SecretKeySpec")) {
                if (result_chest.get("Certificate") != null && result_chest.get("HASH") != null && result_chest.get("salt") != null && result_chest.get("PK: IV") != null && result_chest.get("PK: SecretKeySpec") != null) {
                    if (!result_chest.get("Certificate").isEmpty() && !result_chest.get("HASH").isEmpty() && !result_chest.get("salt").isEmpty() && !result_chest.get("PK: IV").isEmpty() && !result_chest.get("PK: SecretKeySpec").isEmpty()) {

                        // Longing result's contents both in logs and in Kafka
                        log.info("result[PK: IV]: " + Arrays.toString(Base64.getDecoder().decode(result_chest.get("PK: IV"))));
                        log.info("result[PK: SecretKeySpec]: " + Arrays.toString(Base64.getDecoder().decode(result_chest.get("PK: SecretKeySpec"))));

                        log.info("result[Certificate]: " + result_chest.get("Certificate"));
                        log.info("result[HASH]: " + result_chest.get("HASH"));
                        log.info("result[salt]: " + result_chest.get("salt"));

                        kafkaTemplate.send(kafkaConfigs.getSecurity(), "[HANDSHAKE CONTROLLER] Chest Received:");
                        kafkaTemplate.send(kafkaConfigs.getSecurity(), "[HANDSHAKE CONTROLLER] Certificate" + result_chest.get("Certificate"));
                        kafkaTemplate.send(kafkaConfigs.getSecurity(), "[HANDSHAKE CONTROLLER] HASH" + result_chest.get("HASH"));
                        kafkaTemplate.send(kafkaConfigs.getSecurity(), "[HANDSHAKE CONTROLLER] salt" + result_chest.get("salt"));


                        // Using default method compare locally created hash with one that is received
                        boolean checkpw_result = BCrypt.checkpw(result_chest.get("PK: IV") + result_chest.get("PK: SecretKeySpec") + result_chest.get("Certificate"), result_chest.get("HASH"));

                        log.info("checkpw result: " + checkpw_result);
                        kafkaTemplate.send(kafkaConfigs.getSecurity(), "[HANDSHAKE CONTROLLER] checkpw result: " + checkpw_result);

                        if (checkpw_result) {
                            kafkaTemplate.send(kafkaConfigs.getSecurity(), "[HANDSHAKE CONTROLLER] Hashes do match indeed ");
                            proceedHandshake_SendEncryptedMessage(result_chest);
                        } else {
                            kafkaTemplate.send(kafkaConfigs.getSecurity(), "[HANDSHAKE CONTROLLER] Hashes do not match. Handshake stopped.");

                            response.put("Message", "Hashes do not match. Handshake stopped.");

                            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        kafkaTemplate.send(kafkaConfigs.getSecurity(), "[HANDSHAKE CONTROLLER] Payload appears to be empty.");
                        response.put("Message", "Payload appears to be empty.");

                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    kafkaTemplate.send(kafkaConfigs.getSecurity(), "[HANDSHAKE CONTROLLER] Payload seem to have at least one null field.");
                    response.put("Message", "Payload seem to have at least one null field.");

                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            } else {
                kafkaTemplate.send(kafkaConfigs.getSecurity(), "[HANDSHAKE CONTROLLER] Payload does not contain required keys.");
                response.put("Message", "Payload does not contain required keys.");

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } else {
            kafkaTemplate.send(kafkaConfigs.getSecurity(), "[HANDSHAKE CONTROLLER] Payload is null.");
            response.put("Message", "Payload is null.");

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("Message", "The chest was sent successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //    @PostMapping("/proceed-handshake/send_encrypted_message")
    private ResponseEntity<Map<String, String>> proceedHandshake_SendEncryptedMessage(@RequestBody Map<String, String> chest) {
        kafkaTemplate.send(kafkaConfigs.getErrorsSecurity(), "[HANDSHAKE CONTROLLER] Sending encrypted message encountered errors due to poor coding xdd");
        Map<String, String> response = new HashMap<>();

        if (chest.containsKey("PK: IV") && chest.containsKey("PK: SecretKeySpec")) {
            if (chest.get("PK: IV") != null && chest.get("PK: SecretKeySpec") != null) {
                if (!chest.get("PK: IV").isEmpty() && !chest.get("PK: SecretKeySpec").isEmpty()) {
                    camellia.generateSymmetricKeys(Base64.getDecoder().decode(chest.get("PK: IV")),
                            Base64.getDecoder().decode(chest.get("PK: SecretKeySpec")));

                    log.info("[Camellia] IV: " + Base64.getEncoder().encodeToString(camellia.getIvParameterSpec().getIV()));
                    log.info("[Camellia] SecretKeySpec: " + Base64.getEncoder().encodeToString(camellia.getSecretKeySpec().getEncoded()));

                    String plainMessage = "The Dying Message";
                    String encryptedMessage = camellia.enchant(plainMessage);

                    String encryptedMessage_hash = BCrypt.hashpw(encryptedMessage, BCrypt.gensalt());

                    Map<String, String> encryptedMessagePayload = new HashMap<>();

                    encryptedMessagePayload.put("EncryptedMessage", encryptedMessage);
                    encryptedMessagePayload.put("EncryptedMessageHash", encryptedMessage_hash);

                    // #todo 1. add a way to know whom to call
                    Map<String, String> result = handshakePostman.contact("http://localhost:8090/handshake/proceed-handshake/get_encrypted_message", encryptedMessagePayload);

                    if (!result.isEmpty() && result.get("ACK") != null && !result.get("ACK").isEmpty() && result.get("ACK").equals("acknowledgment")) {
                        finishHandshake_sendAck();
                    } else {
                        response.put("Message", "Something wrong with ACK. Handshake refused");

                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    response.put("Message", "Payload seem to be empty.");

                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            } else {
                response.put("Message", "Payload seem to have null fields.");

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

        } else {
            response.put("Message", "Payload does not contain required keys.");

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("Message", "Something wrong on the Server side.");

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //    @PostMapping("/finish-handshake/send_ACK")
    private ResponseEntity<Map<String, String>> finishHandshake_sendAck() {
        Map<String, String> ackPayload = new HashMap<>();

        ackPayload.put("ACK", "acknowledgment");

        Map<String, String> result = handshakePostman.contact("http://localhost:8090/handshake/proceed-handshake/get_ACK", ackPayload);

        log.info("result of getAck: " + result);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Receivers Logic

    //#Todo Add error management for situation where RequestBody is null but not Nullable
    //#Todo Return actual pages not hard coded html-css-js code

    @PostMapping("/start-handshake")
    private ResponseEntity<String> startHandshakes(@RequestBody String message_in) {
        log.info("Handshake requested...");
        log.info("message in: " + message_in);

        if (!message_in.equals("Nee")) {
            log.info("Handshake refused. Reason: Wrong Greeting. Actual Greeting: " + message_in);

            return new ResponseEntity<>("Wrong Greeting. Handshake refused.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Nee Nee", HttpStatus.ACCEPTED);
    }

    // #Todo Add more validation logic
    @PostMapping("/proceed-handshake/get_chest")
    private ResponseEntity<Map<String, String>> proceedHandshake(@RequestBody Map<String, String> chest_in) {

        if (chest_in.containsKey("PK") && chest_in.containsKey("Certificate") && chest_in.containsKey("HASH") && chest_in.containsKey("salt")) {
            if (chest_in.get("PK") != null && chest_in.get("Certificate") != null && chest_in.get("HASH") != null && chest_in.get("salt") != null) {
                if (!chest_in.get("HASH").isEmpty()) {
                    log.info("Chest Received");

                    log.info("Chest contents[PK]: " + chest_in.get("PK"));
                    log.info("Chest contents[Certificate]: " + chest_in.get("Certificate"));
                    log.info("Chest contents[HASH]: " + chest_in.get("HASH"));
                    log.info("Chest contents[salt]: " + chest_in.get("salt"));

                    boolean checkpw_result = false;

                    try {
                        checkpw_result = BCrypt.checkpw(chest_in.get("PK") + chest_in.get("Certificate"), chest_in.get("HASH"));
                    } catch (IllegalArgumentException illegalArgumentException) {
                        log.info("illegalArgumentException occurred while checking_pw. Error message: " + illegalArgumentException.getMessage());

                        Map<String, String> response = new HashMap<>();
                        response.put("Message", "Request validation failed. Something wrong with hash.");

                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }

                    kafkaTemplate.send(kafkaConfigs.getErrorsSecurity(), "Result of checkPW is: " + checkpw_result);
                    if (!checkpw_result) {
                        Map<String, String> response = new HashMap<>();
                        response.put("Message", "Request validation failed. Hashes do not match.");

                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    Map<String, String> response = new HashMap<>();
                    response.put("Message", "Request validation failed. Hash is null or empty.");

                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("Message", "Request validation failed. At least one request field is null.");

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("Message", "Request validation failed. Request does not contain required keys.");

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Map<String, String> chest = new HashMap<>();
        if (camellia != null) {

            log.info("trying to generate symmetric key...");
            camellia.generateSymmetricKeys();

        }

        X509Certificate certificate;

        certificate = certificationGenerator.generateCertification();

        String salt = BCrypt.gensalt();
        String hashedMessage = BCrypt.hashpw(Base64.getEncoder().encodeToString(camellia.getIvParameterSpec().getIV())
                + Base64.getEncoder().encodeToString(camellia.getSecretKeySpec().getEncoded())
                + certificate, salt);

        chest.put("Certificate", certificate.toString());
        chest.put("HASH", hashedMessage);
        chest.put("salt", salt);

        chest.put("PK: IV", Base64.getEncoder().encodeToString(camellia.getIvParameterSpec().getIV()));
        chest.put("PK: SecretKeySpec", Base64.getEncoder().encodeToString(camellia.getSecretKeySpec().getEncoded()));

        log.info("Data to be transferred [Camellia] IV: " + Arrays.toString(camellia.getIvParameterSpec().getIV()));
        log.info("Data to be transferred [Camellia] SecretKeySpec: " + Arrays.toString(camellia.getSecretKeySpec().getEncoded()));

        log.info("Data to be transferred [Camellia] IV: " + Base64.getEncoder().encodeToString(camellia.getIvParameterSpec().getIV()));
        log.info("Data to be transferred [Camellia] SecretKeySpec: " + Base64.getEncoder().encodeToString(camellia.getSecretKeySpec().getEncoded()));

        return new ResponseEntity<>(chest, HttpStatus.ACCEPTED);
    }

    @PostMapping("/proceed-handshake/get_encrypted_message")
    private ResponseEntity<Map<String, String>> proceedHandshake_getEncryptedMessage(@RequestBody Map<String, String> encryptedMessagePayload) {
        log.info("encryptedMessage: " + encryptedMessagePayload.get("EncryptedMessage"));

        Map<String, String> result = new HashMap<>();

        if (encryptedMessagePayload.containsKey("EncryptedMessage") && encryptedMessagePayload.containsKey("EncryptedMessageHash")) {
            if (encryptedMessagePayload.get("EncryptedMessage") != null && encryptedMessagePayload.get("EncryptedMessageHash") != null) {
                if (!encryptedMessagePayload.get("EncryptedMessage").isEmpty() && !encryptedMessagePayload.get("EncryptedMessageHash").isEmpty()) {

                    boolean checkpw_result;

                    try {
                        checkpw_result = BCrypt.checkpw(encryptedMessagePayload.get("EncryptedMessage"), encryptedMessagePayload.get("EncryptedMessageHash"));
                    } catch (IllegalArgumentException illegalArgumentException) {
                        log.info("illegalArgumentException occurred while checking_pw. Error message: " + illegalArgumentException.getMessage());

                        Map<String, String> response = new HashMap<>();
                        response.put("Message", "Request validation failed. Something wrong with hash.");

                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                    }

                    log.info("checkpw_result of encrypted message: " + checkpw_result);
                    log.info("encrypted message hash: " + encryptedMessagePayload.get("EncryptedMessageHash"));
                    if (checkpw_result) {
                        log.info("disenchanted string: " + camellia.disenchant(encryptedMessagePayload.get("EncryptedMessage")));
                    } else {
                        result.put("Message", "Hashes do not match.");

                        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
                    }


                    Map<String, String> response_ack = new HashMap<>();
                    response_ack.put("ACK", "acknowledgment");
                    return new ResponseEntity<>(response_ack, HttpStatus.ACCEPTED);
                } else {
                    result.put("Message", "Payload appears to be empty.");

                    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
                }
            } else {
                result.put("Message", "Payload fields appear to be empty or null.");

                return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }
        } else {
            result.put("Message", "Payload does not contain required keys.");

            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/proceed-handshake/get_ACK")
    private ResponseEntity<Map<String, String>> proceedHandshake_getACK(@RequestBody Map<String, String> ackPayload) {
        log.info("Ack payload content: " + ackPayload);

        Map<String, String> data = new HashMap<>();
        List<Anaata> anaata = anaataRepository.findAll();

        data.put("Data", anaata.toString());
//        for (int i = 0; i < anaata.size(); i++) {
//            Anaata current_anaata = anaata.get(i);
//            data.put("yume", current_anaata.getYume());
//            data.put("message", current_anaata.getMessage());
//            data.put("name", current_anaata.getName());
//            data.put("probability", current_anaata.getProbability());
//        }

//        data.put("Song", "The Dying Message");
//        data.put("Author", "Utsu-P");
//        data.put("Length", "354");
//        data.put("ShortDescription", "Nee anata wa itsumo yume wo mite masu ka? owari kake no kono sekai de Fusagare ta micchi wo hiraku mono ni naru  'Messeji' wo ida ita tsurai yooo");

        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}

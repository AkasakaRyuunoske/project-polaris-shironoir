package projectpolaris.ProjectPolarisShironoir.Handshake;

import jakarta.annotation.Nullable;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.operator.OperatorCreationException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import projectpolaris.ProjectPolarisShironoir.Messaging.KafkaConfigs;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/start-handshake")
    private ResponseEntity<String> startHandshakes(@RequestBody String message_in){
        log.info("Handshake requested...");
        log.info("message in: " + message_in);

        if (!message_in.equals("Nee")){
            log.info("Handshake refused. Reason: Wrong Greeting. Actual Greeting: " + message_in);
            return new ResponseEntity<>("Wrong Greeting. Handshake refused.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Nee Nee", HttpStatus.ACCEPTED);
    }
//    @PostMapping("/proceed-handshake/get_chest")
//    private ResponseEntity<String> proceedHandshakes(@RequestBody Map<String, String> chest_in){
//        log.info("Chest Received");
//
//        log.info("Chest contents[PK]: " + chest_in.get("PK"));
//        log.info("Chest contents[Certificate]: " + chest_in.get("Certificate"));
//        log.info("Chest contents[HASH]: " + chest_in.get("HASH"));
//        log.info("Chest contents[salt]: " + chest_in.get("salt"));
//
//        // #Todo Add validation logic
//        // #Todo Add hash checking logic
//        return proceedHandshake();
//
////        return new ResponseEntity<>("Chest received successfully, expect one in return, baka ><", HttpStatus.ACCEPTED);
//    }

//    @PostMapping("/proceed-handshake/send_chest")
//    private ResponseEntity<String> proceedHandshake(){
//        log.info("Nee Nee Neeee");
//
//        X509Certificate certificate;
//
//        try {
//            certificate = certificationGenerator.generateCertification();
//        } catch (NoSuchAlgorithmException | CertificateException | IOException | OperatorCreationException e) {
//            throw new RuntimeException(e);
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        String salt = BCrypt.gensalt();
//        String hashedMessage = BCrypt.hashpw(certificate.getPublicKey().toString() + certificate.getPublicKey().toString(), salt);
//
//        Map<String, String> chest = new HashMap<>();
//
//        chest.put("PK", certificate.getPublicKey().toString());
//        chest.put("Certificate", certificate.toString());
//        chest.put("HASH", hashedMessage);
//        chest.put("salt", salt);
//
//        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(chest, headers);
//
//        // #todo 1. add a way to know whom to call
//        // #todo 2. put all of this logic into a dedicated service
//        String uri = "http://localhost:8080/handshake/proceed-handshake/get_chest";
//
//        RestTemplate restTemplate = new RestTemplate();
//        String result = restTemplate.postForObject(uri, httpEntity, String.class);
//
//        log.info("result: " + result);
//
//        return new ResponseEntity<>(certificate.toString(), HttpStatus.OK);
//    }

    @PostMapping("/proceed-handshake/send_chest")
    private ResponseEntity<Map<String, String>> proceedHandshake_SendChest(@RequestBody @Nullable Map<String, String> chest_in) {
        log.info("Nee Nee Neeee");

        if (chest_in == null) {
            X509Certificate certificate;

            try {
                certificate = certificationGenerator.generateCertification();
            } catch (NoSuchAlgorithmException | CertificateException | IOException | OperatorCreationException e) {
                throw new RuntimeException(e);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String salt = BCrypt.gensalt();
            String hashedMessage = BCrypt.hashpw(certificate.getPublicKey().toString() + certificate.getPublicKey().toString(), salt);

            Map<String, String> chest = new HashMap<>();

            chest.put("PK", certificate.getPublicKey().toString());
            chest.put("Certificate", certificate.toString());
            chest.put("HASH", hashedMessage);
            chest.put("salt", salt);

            HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(chest, headers);

            // #todo 1. add a way to know whom to call
            // #todo 2. put all of this logic into a dedicated service
            String uri = "http://localhost:8090/handshake/proceed-handshake/get_chest";

            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> result_chest = restTemplate.postForObject(uri, httpEntity, Map.class);

            log.info("result[PK: IV]: " + Arrays.toString(Base64.getDecoder().decode(result_chest.get("PK: IV"))));
            log.info("result[PK: SecretKeySpec]: " + Arrays.toString(Base64.getDecoder().decode(result_chest.get("PK: SecretKeySpec"))));

            Map<String, String> response_map = new HashMap<String, String>();
            response_map.put("OK", "OK");

            return new ResponseEntity<>(response_map, HttpStatus.OK); //#todo might need to change
        } else {

            if (camellia != null) {
                try {
                    log.info("trying to generate symmetric key...");
                    camellia.generateSymmetricKeys();

                } catch (IllegalBlockSizeException e) {
                    log.info("IllegalBlockSizeException was thrown.");
                    throw new RuntimeException(e);

                } catch (BadPaddingException e) {
                    log.info("BadPaddingException was thrown.");
                    throw new RuntimeException(e);

                } catch (InvalidAlgorithmParameterException e) {
                    log.info("InvalidAlgorithmParameterException was thrown.");
                    throw new RuntimeException(e);

                } catch (InvalidKeyException e) {
                    log.info("InvalidKeyException was thrown.");
                    throw new RuntimeException(e);

                } catch (NoSuchPaddingException e) {
                    log.info("NoSuchPaddingException was thrown.");
                    throw new RuntimeException(e);

                } catch (NoSuchAlgorithmException e) {
                    log.info("NoSuchAlgorithmException was thrown.");
                    throw new RuntimeException(e);

                } catch (NoSuchProviderException e) {
                    log.info("NoSuchProviderException was thrown.");
                    throw new RuntimeException(e);
                }
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> chest = new HashMap<>();

            chest.put("PK: IV", Base64.getEncoder().encodeToString(camellia.getIvParameterSpec().getIV()));
            chest.put("PK: SecretKeySpec", Base64.getEncoder().encodeToString(camellia.getSecretKeySpec().getEncoded()));

            log.info("Data to be transferred [Camellia] IV: " + Base64.getEncoder().encodeToString(camellia.getIvParameterSpec().getIV()));
            log.info("Data to be transferred [Camellia] SecretKeySpec: " + Base64.getEncoder().encodeToString(camellia.getSecretKeySpec().getEncoded()));

            HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(chest, headers);

            // #todo 1. add a way to know whom to call
            // #todo 2. put all of this logic into a dedicated service
            String uri = "http://localhost:8090/handshake/proceed-handshake/send_chest";

            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.postForObject(uri, httpEntity, String.class);

            chest.clear();

            chest.put("Message", "Yay we are talking");

            log.info("result is: " + result);

            return new ResponseEntity<>(chest, HttpStatus.OK); //#todo might need to change
        }
    }


    @PostMapping("/proceed-handshake/get_chest")
    private ResponseEntity<Map<String, String>> proceedHandshake(@RequestBody @Nullable Map<String, String> chest_in) {

        if (chest_in != null) {
            log.info("Chest Received");

            log.info("Chest contents[PK]: " + chest_in.get("PK"));
            log.info("Chest contents[Certificate]: " + chest_in.get("Certificate"));
            log.info("Chest contents[HASH]: " + chest_in.get("HASH"));
            log.info("Chest contents[salt]: " + chest_in.get("salt"));

            // #Todo Add validation logic
            // #Todo Add hash checking logic

            kafkaTemplate.send(kafkaConfigs.getErrorsSecurity(), "Result of checkPW is: " + BCrypt.checkpw(chest_in.get("PK") + chest_in.get("Certificate"), chest_in.get("HASH")));
            // if ok send chest logic
        }

        Map<String, String> chest = new HashMap<>();
//        if (camellia != null) {
            try {
                log.info("trying to generate symmetric key...");
                camellia.generateSymmetricKeys();

            } catch (IllegalBlockSizeException e) {
                log.info("IllegalBlockSizeException was thrown.");
                throw new RuntimeException(e);

            } catch (BadPaddingException e) {
                log.info("BadPaddingException was thrown.");
                throw new RuntimeException(e);

            } catch (InvalidAlgorithmParameterException e) {
                log.info("InvalidAlgorithmParameterException was thrown.");
                throw new RuntimeException(e);

            } catch (InvalidKeyException e) {
                log.info("InvalidKeyException was thrown.");
                throw new RuntimeException(e);

            } catch (NoSuchPaddingException e) {
                log.info("NoSuchPaddingException was thrown.");
                throw new RuntimeException(e);

            } catch (NoSuchAlgorithmException e) {
                log.info("NoSuchAlgorithmException was thrown.");
                throw new RuntimeException(e);

            } catch (NoSuchProviderException e) {
                log.info("NoSuchProviderException was thrown.");
                throw new RuntimeException(e);
            }
//        }
        X509Certificate certificate;

        try {
            certificate = certificationGenerator.generateCertification();
        } catch (NoSuchAlgorithmException | CertificateException | IOException | OperatorCreationException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String salt = BCrypt.gensalt();
        String hashedMessage = BCrypt.hashpw(Base64.getEncoder().encodeToString(camellia.getIvParameterSpec().getIV())
                + Base64.getEncoder().encodeToString(camellia.getSecretKeySpec().getEncoded())
                + certificate.getPublicKey().toString() + certificate, salt);

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
    private ResponseEntity<Map<String, String>> proceedHandshake_getEncryptedMessage(@RequestBody Map<String, String> encryptedMessagePayload){
        log.info("encryptedMessage: " + encryptedMessagePayload.get("EncryptedMessage"));
        try {
            log.info("disenchated string: " + camellia.disenchant(encryptedMessagePayload.get("EncryptedMessage")));
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }

        Map<String, String> response_ack = new HashMap<>();
        response_ack.put("ACK", "acknowledgment");

        return new ResponseEntity<>(response_ack, HttpStatus.ACCEPTED);
    }
}

package projectpolaris.ProjectPolarisShironoir.Handshake;

import jakarta.annotation.Nullable;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Log4j2
@RestController
@RequestMapping("/handshake")
public class HandshakeController {
    @PostMapping("/start-handshake")
    private ResponseEntity<String> startHandshakes(@RequestBody String message_in){
        log.info("Handshake requested...");
        log.info("message in: " + message_in);

        return new ResponseEntity<>("Nee Nee", HttpStatus.ACCEPTED);
    }
    @PostMapping("/proceed-handshake")
    private ResponseEntity<String> proceedHandshake(){
        log.info("Nee Nee Neeee");

        return new ResponseEntity<>("", HttpStatus.CONTINUE);
    }
}

package projectpolaris.ProjectPolarisShironoir.Handshake;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class test {
    @PostMapping("/testmessage")
    private ResponseEntity<String> tesmessage() {

        log.info("Was called");

        return new ResponseEntity<>("Nee Nee", HttpStatus.OK);
    }
}

package projectpolaris.ProjectPolarisShironoir.Handshake.SecurityServices;

import org.springframework.stereotype.Service;

@Service
public class GenerateACK {
    public String generateACK(){
        String ack = "asd";
        //Idea:
        //  Use Current Data in millis(last 4 digits) -> first 2 for a starting word and last 2(+ current month number) for last word position
        //  Get last message from kafka (utsup topic), from which sequentially get characters until it matches with first letter of any
        //  Utsu-P's album and check if it is not same as in the message before.
        //
        //  If kafka is empty or is unreachable #todo decide what to do: 1. use default ack or 2. Refuse handshake
        //
        //  (Example: last message in kafka is -> "Error 500 managed in [Shironoir]"
        //  0. get last message from "utsup" topic
        //  1. go through whole message e -> [r] -> r -> o -> r -> 5 -> 0 -> 0 -> m -> [a]
        //                                    ↑                                         ↑
        // If the letter of last message was R[Renaissance], it ignores it and goes to the next matcher A[Algorithm]

        return ack;
    }
}

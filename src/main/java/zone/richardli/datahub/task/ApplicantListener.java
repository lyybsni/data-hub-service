package zone.richardli.datahub.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicantListener {

    @KafkaListener(id = "listen", topics = "myTopic")
    public void listen() {
        log.info("ACKed");
    }

}

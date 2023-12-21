package zone.richardli.datahub.component;

import lombok.extern.slf4j.Slf4j;

/**
 * This component will implement Kafka listeners.
 */
@Slf4j
// @Component
public class ApplicantListener {

    // @KafkaListener(id = "listen", topics = "myTopic")
    public void listen() {
        log.info("ACKed");
    }

}

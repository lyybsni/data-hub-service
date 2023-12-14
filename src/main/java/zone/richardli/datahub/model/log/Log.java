package zone.richardli.datahub.model.log;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
public class Log {
    @Id
    private String id;

    @Property
    private OffsetDateTime time;

    @Property
    private String operation;

    @Property
    private String operator;

    @Property
    private String remarks;

    @Property
    private LogStatus status;

}

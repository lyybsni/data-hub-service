package zone.richardli.datahub.model.schema.schema;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Entity("schemas")
@NoArgsConstructor
@AllArgsConstructor
public class SchemaPO {

    @Id
    private String id;

    @Property
    private String schema;

    @Property
    private OffsetDateTime createdAt;

    @Property
    private OffsetDateTime updatedAt;

    private int version = 1;
}

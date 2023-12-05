package zone.richardli.datahub.model.schema;

import dev.morphia.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity("schemamappings")
@NoArgsConstructor
@AllArgsConstructor
public class SchemaMappingPO {

    @Id
    private String id;

    @Property
    private String mapping;

    @Reference
    private SchemaPO schema;

}

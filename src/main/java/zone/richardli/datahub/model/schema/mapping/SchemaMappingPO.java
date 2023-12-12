package zone.richardli.datahub.model.schema.mapping;

import dev.morphia.annotations.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zone.richardli.datahub.model.common.FieldDefinition;
import zone.richardli.datahub.model.schema.schema.SchemaPO;

import java.util.List;

@Data
@Entity("schemamappings")
@NoArgsConstructor
@AllArgsConstructor
public class SchemaMappingPO {

    @Id
    private String id;

    @Property
    private List<FieldDefinition> mapping;

    @Reference
    private SchemaPO schema;

}

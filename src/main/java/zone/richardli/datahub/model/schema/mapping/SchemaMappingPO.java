package zone.richardli.datahub.model.schema.mapping;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zone.richardli.datahub.model.common.FieldDefinition;
import zone.richardli.datahub.model.schema.schema.SchemaPO;

import java.time.OffsetDateTime;
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

    @Property
    private String collection;

    @Property
    private List<String> primaryKey;

    @Reference
    private SchemaPO schema;

    @Property
    private OffsetDateTime createdAt;

    @Property
    private OffsetDateTime updatedAt;

}

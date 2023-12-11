package zone.richardli.datahub.model.schema;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zone.richardli.datahub.model.common.FieldDefinition;

import java.util.List;

@Data
@Entity("schemas")
@NoArgsConstructor
@AllArgsConstructor
public class SchemaPO {

    @Id
    private String id;

    /**
     * @deprecated move to mappings for better resolve
     */
    @Property
    private String schema;

    @Property
    private List<FieldDefinition> mappings;

}

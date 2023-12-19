package zone.richardli.datahub.model.schema.mapping;

import lombok.Data;
import zone.richardli.datahub.model.common.FieldDefinition;

import java.util.Map;

@Data
public class SchemaMappingVO {

    private Map<String, FieldDefinition> mapping;

    private String schemaId;

    private String mappingId;

    private String displayName;

}

package zone.richardli.datahub.model.schema;

import lombok.Data;
import zone.richardli.datahub.model.common.FieldDefinition;

import java.util.Map;

@Data
public class SchemaVO {

    private String id;

    private Map<String, FieldDefinition> schema;

}

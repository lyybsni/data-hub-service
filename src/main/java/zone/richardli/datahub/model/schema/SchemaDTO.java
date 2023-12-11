package zone.richardli.datahub.model.schema;

import lombok.Data;
import zone.richardli.datahub.model.common.FieldDefinition;

import java.util.Date;
import java.util.List;

@Data
public class SchemaDTO {

    private String id;

    private List<FieldDefinition> schema;

    /**
     * For displaying usage
     */
    private Date updatedAt;

}

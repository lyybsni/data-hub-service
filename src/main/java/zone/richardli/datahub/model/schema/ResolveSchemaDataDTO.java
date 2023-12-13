package zone.richardli.datahub.model.schema;

import lombok.Data;
import zone.richardli.datahub.model.schema.schema.TreeNode;

import java.util.List;

@Data
public class ResolveSchemaDataDTO {

    private String name;

    private List<TreeNode> fields;

}

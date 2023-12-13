package zone.richardli.datahub.model.schema.schema;

import lombok.Data;

@Data
public class TreeNode {

    private String id;

    private String path;

    private boolean isArray = false;

    private String name;

    private String type;

}

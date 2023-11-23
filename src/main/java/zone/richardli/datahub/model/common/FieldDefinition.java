package zone.richardli.datahub.model.common;

import lombok.Data;

@Data
public class FieldDefinition {

    private boolean primary;

    private String from;

    private String expression;

}

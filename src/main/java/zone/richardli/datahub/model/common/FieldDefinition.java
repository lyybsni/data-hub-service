package zone.richardli.datahub.model.common;

import java.util.List;
import lombok.Data;

@Data
public class FieldDefinition {

    private boolean primary;

    /**
     * Directly copy a field into the target field.
     * e.g. root.application.applicationId
     */
    private String inherit;

    /**
     * Compute the field based on the variables.
     * e.g. `$1 + " " + $2`
     */
    private String expression;

    /**
     * Provide the variables for field expression.
     * e.g. ["root.application.firstName", "root.application.lastName"]
     */
    private List<String> variables;

    /**
     * Describe the capturing rules:
     * e.g. "^(:.*?)\s(:.*?)$"
     */
    private String capturingRegex;

    /**
     * Describe the target output:
     * e.g. "$1"
     */
    private String targetRegex;

}

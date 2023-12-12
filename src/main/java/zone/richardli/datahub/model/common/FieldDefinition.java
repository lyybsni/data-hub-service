package zone.richardli.datahub.model.common;

import java.util.List;
import java.util.Map;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import lombok.Data;

@Data
@Embedded
public class FieldDefinition {

    @Property
    private String path;

    @Property
    private boolean primary = false;

    /**
     * Directly copy a field into the target field.
     * e.g. root.application.applicationId
     */
    @Property
    private String inherit;

    /**
     * Compute the field based on the variables.
     * e.g. `$1 + " " + $2`
     */
    @Property
    private String expression;

    /**
     * Provide the variables for field expression.
     * e.g. ["root.application.firstName", "root.application.lastName"]
     */
    @Property
    private Map<String, String> variables;

    @Property
    private String regex;

    /**
     * Describe the capturing rules:
     * e.g. "^(:.*?)\s(:.*?)$"
     */
    @Property
    private String capturingRegex;

    /**
     * Describe the target output:
     * e.g. "$1"
     */
    @Property
    private String targetRegex;

}

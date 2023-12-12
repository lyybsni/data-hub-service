package zone.richardli.datahub.model.schema.trail;

import lombok.Data;

@Data
public class ConversionTrailRun {

    private String schemaId;

    private String mappingId;

    private String json;

}

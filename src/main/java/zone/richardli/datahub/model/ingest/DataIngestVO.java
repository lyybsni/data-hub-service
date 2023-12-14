package zone.richardli.datahub.model.ingest;

import lombok.Data;

@Data
public class DataIngestVO {

    private String clientId;

    private String mappingId;

    private Object[] data;

}

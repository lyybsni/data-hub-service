package zone.richardli.datahub.model.ingest;

import lombok.Data;

@Data
public class DataIngestDTO {

    private String targetCollectionName;

    private int affectedRows;

}

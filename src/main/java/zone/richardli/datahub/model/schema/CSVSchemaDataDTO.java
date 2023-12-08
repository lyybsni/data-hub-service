package zone.richardli.datahub.model.schema;

import lombok.Data;

import java.util.List;

@Data
public class CSVSchemaDataDTO {

    private String name;

    private List<Field> fields;

    @Data
    public static class Field {

        private String name;

        private String type;

    }

}

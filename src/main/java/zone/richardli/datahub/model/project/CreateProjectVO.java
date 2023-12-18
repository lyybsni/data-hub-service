package zone.richardli.datahub.model.project;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateProjectVO {

    private String name;

    private String contactPerson;

    private String clientId;

}

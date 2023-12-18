package zone.richardli.datahub.model.project;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Project {

    @Id
    private String id;

    @Property
    private String name;

    @Property
    private String contactPerson;

    @Property
    private String clientId;

    @Property
    private boolean whiteList;

    @Property
    private List<String> specifiedSchemas;

}

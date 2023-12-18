package zone.richardli.datahub.model.user;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.Data;

@Data
@Entity
public class User {

    @Id
    private String id;

    /**
     * Username of the user
     */
    @Property
    private String username;

    /**
     * Password of the user
     */
    @Property
    private String password;

    /**
     * Role of the user
     */
    @Property
    private UserRole role;

    /**
     * Privilege type of the user
     */
    @Property
    PrivilegeType privilegeType;
}

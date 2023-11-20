package zone.richardli.datahub.model;

import lombok.Data;

@Data
public class User {
    /**
     * Username of the user
     */
    private String username;

    /**
     * Password of the user
     */
    private String password;

    /**
     * Roles of the user
     * TODO: change to Role type
     */
    private String[] roles;
}

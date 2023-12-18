package zone.richardli.datahub.model.user;

import lombok.Data;

@Data
public class CreateUserVO {

    private String username;

    private String password;

    private UserRole role = UserRole.END_USER;

    private PrivilegeType privilegeType = PrivilegeType.READ;

}

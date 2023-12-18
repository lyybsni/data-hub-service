package zone.richardli.datahub.model.user;

import lombok.Data;

@Data
public class UpdateUserVO {

    private String id;

    private UserRole role = UserRole.END_USER;

    private PrivilegeType privilegeType = PrivilegeType.READ;

}

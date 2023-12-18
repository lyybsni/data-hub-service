package zone.richardli.datahub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zone.richardli.datahub.model.project.Project;
import zone.richardli.datahub.model.user.PrivilegeType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    public boolean hasWritePrivilege(String userId) {
        PrivilegeType type = userService.getUserById(userId).getPrivilegeType();
        return type == PrivilegeType.WRITE || type == PrivilegeType.READ_WRITE;
    }

    public boolean hasSchemaPrivilege(String clientId, String schemaId) {
        Project project = userService.getProjectByClientId(clientId);

        if (project.isWhiteList()) {
            return project.getSpecifiedSchemas().contains(schemaId);
        } else {
            // if the user is forbidden to modify the schema, then the user is not allowed to access it
            return ! project.getSpecifiedSchemas().contains(schemaId);
        }
    }

}

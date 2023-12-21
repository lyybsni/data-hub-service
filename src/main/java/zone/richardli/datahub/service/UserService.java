package zone.richardli.datahub.service;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zone.richardli.datahub.model.project.CreateProjectVO;
import zone.richardli.datahub.model.project.Project;
import zone.richardli.datahub.model.project.UpdateProjectVO;
import zone.richardli.datahub.model.user.CreateUserVO;
import zone.richardli.datahub.model.user.UpdateUserVO;
import zone.richardli.datahub.model.user.User;
import zone.richardli.datahub.utility.IdUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final Datastore datastore;

    protected User getUserById(String id) {
        return datastore.find(User.class)
                .filter(Filters.eq("id", id))
                .first();
    }

    protected Project getProjectById(String id) {
        return datastore.find(Project.class)
                .filter(Filters.eq("id", id))
                .first();
    }

    protected Project getProjectByClientId(String clientId) {
        return datastore.find(Project.class)
                .filter(Filters.eq("clientId", clientId))
                .first();
    }

    public List<User> userList() {
        return datastore.find(User.class)
                .iterator()
                .toList();
    }

    public List<Project> projectList() {
        return datastore.find(Project.class)
                .iterator()
                .toList();
    }

    public void createUser(CreateUserVO vo) {
        User user = new User();
        user.setId(IdUtils.generateId());
        user.setUsername(vo.getUsername());
        user.setPassword(vo.getPassword());
        user.setRole(vo.getRole());
        user.setPrivilegeType(vo.getPrivilegeType());
        datastore.save(user);
    }

    public void createProject(CreateProjectVO vo) {
        Project project = new Project();
        project.setId(IdUtils.generateId());
        project.setName(vo.getName());
        project.setContactPerson(vo.getContactPerson());
        project.setClientId(IdUtils.generateClientId());
        datastore.save(project);
    }

    public void assignUserPrivileges(UpdateUserVO vo) {
        datastore.find(User.class)
                .filter(Filters.eq("id", vo.getId()))
                .update(UpdateOperators.set("role", vo.getRole()),
                        UpdateOperators.set("privilegeType", vo.getPrivilegeType()))
                .execute();
    }

    public void assignProjectPrivileges(UpdateProjectVO vo) {
        datastore.find(Project.class)
                .filter(Filters.eq("id", vo.getId()))
                .update(UpdateOperators.set("whiteList", vo.isWhiteList()),
                        UpdateOperators.set("specifiedSchemas", vo.getSpecifiedSchemas()))
                .execute();
    }

}

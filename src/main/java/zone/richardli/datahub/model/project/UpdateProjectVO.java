package zone.richardli.datahub.model.project;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdateProjectVO {

    private String id;

    private boolean whiteList;

    private List<String> specifiedSchemas;

}

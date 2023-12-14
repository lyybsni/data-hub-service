package zone.richardli.datahub.service;

import com.opencsv.exceptions.CsvException;
import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import zone.richardli.datahub.advise.exceptions.InvalidInputException;
import zone.richardli.datahub.model.log.Loggable;
import zone.richardli.datahub.model.schema.ResolveSchemaDataDTO;
import zone.richardli.datahub.model.schema.mapping.SchemaMappingPO;
import zone.richardli.datahub.model.schema.mapping.SchemaMappingVO;
import zone.richardli.datahub.model.schema.schema.SchemaPO;
import zone.richardli.datahub.model.schema.schema.SchemaVO;
import zone.richardli.datahub.model.schema.schema.TreeNode;
import zone.richardli.datahub.task.CSVReader;
import zone.richardli.datahub.utility.IdUtil;
import zone.richardli.datahub.utility.JSONUtils;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final Datastore datastore;

    private final CSVReader csvReader;

    @Loggable
    public String saveSchema(SchemaVO vo) {
        String id = IdUtil.generateId();
        datastore.save(new SchemaPO(
                id,
                vo.getSchema(),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        ));
        return id;
    }

    @Loggable
    public void updateSchema(SchemaVO vo) {
        datastore.find(SchemaPO.class)
                .filter(Filters.eq("_id", vo.getId()))
                .update(UpdateOperators.set("schema", vo.getSchema()),
                        UpdateOperators.set("updatedAt", OffsetDateTime.now()))
                .execute();
    }

    public List<SchemaPO> getSchema(String id) {
        return StreamSupport.stream(datastore.find(SchemaPO.class)
                        .filter(Filters.eq("id", id))
                        .spliterator(), true)
                .collect(Collectors.toList());
    }

    public List<SchemaPO> getSchemas() {
        return StreamSupport.stream(datastore.find(SchemaPO.class).spliterator(), true).collect(Collectors.toList());
    }

    @Loggable
    public String saveSchemaMapping(SchemaMappingVO vo) {
        String id = IdUtil.generateId();
        SchemaPO temp = new SchemaPO();
        temp.setId(vo.getSchemaId());
        vo.getMapping().forEach((k, v) -> v.setPath(k));
        datastore.save(new SchemaMappingPO(
                id,
                new ArrayList<>(vo.getMapping().values()),
                temp,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        ));
        return id;
    }

    @Loggable
    public void updateSchemaMapping(SchemaMappingVO vo) {
        datastore.find(SchemaMappingPO.class)
                .filter(Filters.eq("_id", vo.getMappingId()))
                .update(
                        UpdateOperators.set("mapping", new ArrayList<>(vo.getMapping().values())),
                        UpdateOperators.set("updatedAt", OffsetDateTime.now())
                )
                .execute();
    }

    public List<SchemaMappingPO> getSchemaMapping(String id) {
        return StreamSupport.stream(datastore.find(SchemaMappingPO.class)
                        .filter(Filters.eq("id", id))
                        .spliterator(), true)
                .collect(Collectors.toList());
    }

    public List<SchemaMappingPO> getRelatedSchemaMapping(String id) {
        // id is the schema id
        return StreamSupport.stream(datastore.find(SchemaMappingPO.class)
                        .filter(Filters.eq("schema", id))
                        .spliterator(), true)
                .collect(Collectors.toList());
    }

    @Loggable
    public ResolveSchemaDataDTO resolveCSVSchema(MultipartFile file) {
        String originalName = Objects.requireNonNull(file.getOriginalFilename());
        if (!originalName.endsWith(".csv")) {
            throw new InvalidInputException("Unsupported file type. Currently only support .csv file.");
        }

        ResolveSchemaDataDTO dto = new ResolveSchemaDataDTO();

        dto.setName(originalName.split(".csv")[0]);
        try {
            Map<String, Object> input = (Map<String, Object>) csvReader.readFileCSV(file).toArray()[0];
            dto.setFields(input.entrySet().stream().map(entry -> {
                TreeNode field = new TreeNode();
                field.setId(IdUtil.generateId());
                field.setPath(entry.getKey());
                field.setName(entry.getKey());
                field.setType(entry.getValue().getClass().getSimpleName());
                return field;
            }).collect(Collectors.toList()));
        } catch (IOException | CsvException e) {
            throw new InvalidInputException("Invalid file input",e);
        }

        return dto;
    }

    @Loggable
    public ResolveSchemaDataDTO resolveJSONSchema(MultipartFile file) {
        ResolveSchemaDataDTO dto = new ResolveSchemaDataDTO();
        dto.setName(Objects.requireNonNull(file.getOriginalFilename()).split(".json")[0]);

        Map<String, Object> mmp;
        try {
            mmp = JSONUtils.parseJSONTree(csvReader.readFile(file), false);
        } catch (IOException e) {
            throw new InvalidInputException("Invalid file input. Please check if the file is corrupted.");
        }

        List<TreeNode> mmpCopy = new ArrayList<>();
        mmp.forEach((path, res) -> {
            if (path.isEmpty()) {
                throw new InvalidInputException("Invalid field name.");
            }

            TreeNode node = new TreeNode();
            String[] subPaths = path.split("\\.");
            String possibleName = subPaths[subPaths.length - 1];

            boolean isArray = possibleName.endsWith("[0]");
            String name = possibleName.replace("[0]", "");
            String type = isArray ? "Array" : (String) res;
            String id = IdUtil.generateId();

            node.setArray(isArray);
            node.setId(id);
            node.setName(name);
            node.setPath(path);
            node.setType(type);
            mmpCopy.add(node);
        });
        dto.setFields(mmpCopy);

        return dto;
    }
}

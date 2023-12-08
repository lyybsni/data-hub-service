package zone.richardli.datahub.service;

import com.google.gson.Gson;
import com.opencsv.exceptions.CsvException;
import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import zone.richardli.datahub.model.common.JSONDataInput;
import zone.richardli.datahub.model.schema.*;
import zone.richardli.datahub.task.CSVReader;
import zone.richardli.datahub.utility.IdUtil;

import java.io.IOException;
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

    public String saveSchema(SchemaVO vo) {
        String id = IdUtil.generateId();
        log.info("ID is {}", id);
        datastore.save(new SchemaPO(id, vo.getSchema()));
        return id;
    }

    public void updateSchema(SchemaVO vo) {
        datastore.find(SchemaPO.class)
                .filter(Filters.eq("_id", vo.getId()))
                .update(UpdateOperators.set("schema", vo.getSchema()))
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

    public String saveSchemaMapping(SchemaMappingVO vo) {
        String id = IdUtil.generateId();
        SchemaPO temp = new SchemaPO();
        temp.setId(vo.getSchemaId());

        datastore.save(new SchemaMappingPO(id, vo.getMapping(), temp));
        return id;
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

    public CSVSchemaDataDTO resolveCSVSchema(MultipartFile file) {
        CSVSchemaDataDTO dto = new CSVSchemaDataDTO();
        dto.setName(Objects.requireNonNull(file.getOriginalFilename()).split(".csv")[0]);
        try {
            Map<String, Object> input = (Map<String, Object>) csvReader.readFileCSV(file).toArray()[0];

            dto.setFields(input.entrySet().stream().map(entry -> {
                CSVSchemaDataDTO.Field field = new CSVSchemaDataDTO.Field();
                field.setName(entry.getKey());
                field.setType(entry.getValue().getClass().getSimpleName());
                return field;
            }).collect(Collectors.toList()));

            log.info("{}", input);
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }

        return dto;
    }
}

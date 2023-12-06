package zone.richardli.datahub.service;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zone.richardli.datahub.model.schema.SchemaMappingPO;
import zone.richardli.datahub.model.schema.SchemaMappingVO;
import zone.richardli.datahub.model.schema.SchemaPO;
import zone.richardli.datahub.model.schema.SchemaVO;
import zone.richardli.datahub.utility.IdUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final Datastore datastore;

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


}

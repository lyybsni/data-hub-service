package zone.richardli.datahub.deprecated.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zone.richardli.datahub.deprecated.applicant.ApplicantPO;
import zone.richardli.datahub.model.common.FieldDefinition;
import zone.richardli.datahub.service.ApplicantService;
import zone.richardli.datahub.service.POSaver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @since Dec 5
 * @deprecated Dec 14
 * This service should utilize Spark to operate on MongoDB,
 * currently it is not implemented but using the ODM solution
 */
@Slf4j
@Service
@Deprecated
public class MockApplicantSparkService {

    private static final Map<String, Class> m = new HashMap<>();

    private static final Map<String, POSaver> mc = new HashMap<>();

    static {
        m.put("applicant", ApplicantPO.class);
        m.put("output", Object.class);
    }

    private final ApplicantService applicantService;

    private MockApplicantSparkService(ApplicantService applicantService) {
        this.applicantService = applicantService;

        mc.put("applicant", applicantService);
    }

    private static final Gson gson = new Gson();

    public List<Object> mockedSparkBuilder(Object schema, Object[] data, String target) {
        // for current stage, we do not consider recursive structures yet
        LinkedTreeMap<String, FieldDefinition> result = parseSchema(schema);

        log.info("{}", result);
        List<Object> list = Arrays.stream(data).map(item -> constructDataMapping(result, item, target)).collect(Collectors.toList());
        mc.get(target).batchInsertOrUpdate(list);
        return list;
    }

    public List<Object> mockedSparkReader(Object schema, String target) {
        LinkedTreeMap<String, FieldDefinition> result = parseSchema(schema);
        POSaver resource = mc.get(target);
        return (List<Object>) resource.batchRead().stream()
                .map(item -> constructDataMapping(result, item, "output"))
                .collect(Collectors.toList());
    }


    private LinkedTreeMap<String, FieldDefinition> parseSchema(Object schema) {
        LinkedTreeMap<String, Object> temp = gson.fromJson(gson.toJson(schema), LinkedTreeMap.class);
        LinkedTreeMap<String, FieldDefinition> result = new LinkedTreeMap<>();
        temp.forEach((k, v) -> {
            result.put(k, gson.fromJson(gson.toJson(v), FieldDefinition.class));
        });
        return result;
    }

    private Object constructDataMapping(LinkedTreeMap<String, FieldDefinition> schema, Object data, String target) {
        HashMap<String, Object> datammap = gson.fromJson(gson.toJson(data), HashMap.class);

        JsonElement element = gson.toJsonTree(new HashMap<String, Object>());
        JsonObject object = element.getAsJsonObject();

        schema.forEach((k, v) -> {
            if (v.getExpression() != null) {
                // TODO: the expression part should be well considered
                object.add(k, gson.toJsonTree(datammap.get("firstname") + " " + datammap.get("lastname")));
            } else {
                object.add(k, gson.toJsonTree(datammap.get(v.getInherit())));
            }
        });

        return gson.fromJson(element, m.get(target));
    }

}

package zone.richardli.datahub.utility;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import zone.richardli.datahub.model.common.FieldDefinition;
import zone.richardli.datahub.service.strategy.IMappingRule;

import java.util.*;

@Slf4j
public class JSONUtils {

    private static final Gson gson = new Gson();

    /**
     * Convert JSON string to Map in the form of (Path, Content)
     */
    public static Map<String, Object> parseJSONTree(String jsonString) {
        return parseJSONTree(jsonString, true);
    }

    public static Map<String, Object> parseJSONTree(String jsonString, boolean realData) {
        JsonElement jsonElement = gson.fromJson(jsonString, JsonElement.class);
        Map<String, Object> result = new HashMap<>();
        constructJsonHelper(jsonElement, result, "", realData);
        log.info("{}", result);
        return result;
    }


    /**
     * Construct an object from a JSON map and a list of rules.
     *
     * @param jsonMap     the plain JSON map
     * @param rules       the list of rules
     * @param targetClass the target class
     * @param <T>         the type of the target class
     * @return the constructed object
     * @throws JSONException if the JSON map is invalid
     */
    public static <T> T constructObject(Map<String, Object> jsonMap, List<FieldDefinition> rules, Class<T> targetClass) throws JSONException {
        return gson.fromJson(mapToJSONObject(jsonMap, rules).toString(), targetClass);
    }

    public static List<Object> retrieveArrayData(Map<String, Object> jsonMap) {
        return new ArrayList<>();
    }

    private static JSONObject mapToJSONObject(Map<String, Object> map, List<FieldDefinition> rules) throws JSONException {
        rules.sort(Comparator.comparing(FieldDefinition::getPath));

        JSONObject object = new JSONObject();
        for (FieldDefinition rule : rules) {
            String path = rule.getPath();
            String[] pathArray = path.split("\\.");

            JSONObject ref = object;
            for (int i = 0; i < pathArray.length - 1; i++) {
                if (ref.has(pathArray[i])) {
                    ref = ref.getJSONObject(pathArray[i]);
                } else {
                    JSONObject newObject = new JSONObject();
                    ref.put(pathArray[i], newObject);
                    ref = newObject;
                }
            }
            ref.put(pathArray[pathArray.length - 1], IMappingRule.getRule(rule).apply(map, rule));
        }

        return object;
    }

    private static void constructJsonHelper(JsonElement element, Map<String, Object> jsonMap, String parentPath, boolean realdata) {

        if (element.isJsonArray()) {
            // case 1: the object is an array
            JsonArray asJsonArray = element.getAsJsonArray();
            if (realdata) {
                for (int i = 0; i < asJsonArray.size(); i++) {
                    constructJsonHelper(asJsonArray.get(i), jsonMap, parentPath + "[" + i + "]", realdata);
                }
            } else {
                // only record the type of the array
                constructJsonHelper(asJsonArray.get(0), jsonMap, parentPath + "[0]", realdata);
            }
        } else if (element.isJsonPrimitive()) {
            // case 2: the object is a primitive
            if (element.getAsJsonPrimitive().isBoolean()) {
                if (realdata) jsonMap.put(parentPath, element.getAsBoolean());
                else jsonMap.put(parentPath, "Boolean");
            } else if (element.getAsJsonPrimitive().isNumber()) {
                if (realdata) jsonMap.put(parentPath, element.getAsNumber());
                else jsonMap.put(parentPath, "Number");
            } else {
                if (realdata) jsonMap.put(parentPath, element.getAsString());
                else jsonMap.put(parentPath, "String");
            }
        } else if (element.isJsonObject()) {
            // case 3: the object is an object
            JsonObject asJsonObject = element.getAsJsonObject();
            asJsonObject.keySet().forEach(key -> {
                if (parentPath.isEmpty()) {
                    constructJsonHelper(asJsonObject.get(key), jsonMap, key, realdata);
                } else
                    constructJsonHelper(asJsonObject.get(key), jsonMap, parentPath + "." + key, realdata);
            });
        } else if (!element.isJsonNull()) {
            // case 4: the object is null.
            // no operation should be done.
            throw new RuntimeException("Unknown JSON element type: " + element.getClass().getName());
        }

    }

}

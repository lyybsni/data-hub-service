package zone.richardli.datahub.service.strategy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import zone.richardli.datahub.model.common.FieldDefinition;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RegexMappingTest {

    private final RegexMapping regexMapping = new RegexMapping();

    @Test
    void test_mapping() {
        Map<String, Object> source = new HashMap<>();
        source.put("a", "hhhhhhheeeeeellllllooooo");

        FieldDefinition definition = new FieldDefinition();
        definition.setRegex("a");
        definition.setCapturingRegex("(h+)");
        definition.setTargetRegex("x");

        Object result = regexMapping.apply(source, definition);
        log.info("result: {}", result);
    }


}

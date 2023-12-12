package zone.richardli.datahub.service.strategy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import zone.richardli.datahub.service.strategy.ExpressionMapping;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ExpressionMappingTest {

    static ExpressionMapping rule = new ExpressionMapping();

    @Test
    void test_mapping() {
        Map<String, Object> source = new HashMap<>();

        String expression = "${1} + ' ' + ${2}";

        source.put("${1}", "hello");
        source.put("${2}", "world");

        Assertions.assertEquals("hello world", rule.compute(source, expression));
    }


}

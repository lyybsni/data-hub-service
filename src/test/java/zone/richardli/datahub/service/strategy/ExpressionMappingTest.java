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

    @Test
    void test_convert_date() {
        Map<String, Object> source = new HashMap<>();

        String expression = "T(java.time.OffsetDateTime).parse(${1})";

        source.put("${1}", "2023-11-24T06:27:37.406Z");

        log.info("{}", rule.compute(source, expression));
    }


}

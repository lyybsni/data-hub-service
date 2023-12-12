package zone.richardli.datahub.service.strategy;

import lombok.extern.slf4j.Slf4j;
import zone.richardli.datahub.model.common.FieldDefinition;

import java.util.Map;
@Slf4j
public class RegexMapping implements IMappingRule {
    @Override
    public Object apply(Map<String, Object> source, FieldDefinition rule) {
        String from = rule.getCapturingRegex();
        String to = rule.getTargetRegex();
        return ((String) source.get(rule.getRegex())).replaceAll(from, to);
    }
}

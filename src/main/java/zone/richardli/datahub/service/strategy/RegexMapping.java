package zone.richardli.datahub.service.strategy;

import lombok.extern.slf4j.Slf4j;
import zone.richardli.datahub.model.common.FieldDefinition;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class RegexMapping implements IMappingRule {
    @Override
    public Object apply(Map<String, Object> source, FieldDefinition rule) {
        String from = rule.getCapturingRegex();
        String to = rule.getTargetRegex();

        Pattern fromPattern = Pattern.compile(from);
        Matcher matcher = fromPattern.matcher((String) source.get(rule.getRegex()));

        // TODO
        matcher.results().forEach(result -> {
            log.info("{}", result);
        });
        return null;
    }
}

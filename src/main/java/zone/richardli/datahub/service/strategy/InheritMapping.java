package zone.richardli.datahub.service.strategy;

import zone.richardli.datahub.model.common.FieldDefinition;

import java.util.Map;

public class InheritMapping implements IMappingRule {
    @Override
    public Object apply(Map<String, Object> source, FieldDefinition rule) {
        return source.get(rule.getInherit());
    }
}

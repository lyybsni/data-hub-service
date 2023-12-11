package zone.richardli.datahub.service.strategy;

import zone.richardli.datahub.model.common.FieldDefinition;

import java.util.Map;

public interface IMappingRule {

    static IMappingRule getRule(FieldDefinition rule) {
        if (rule.getInherit() != null) {
            return new InheritMapping();
        } else if (rule.getExpression() != null) {
            return new ExpressionMapping();
        } else if (rule.getCapturingRegex() != null) {
            return new RegexMapping();
        } else {
            throw new RuntimeException("Unsupported mapping rule.");
        }
    }

    Object apply(Map<String, Object> source, FieldDefinition rule);

}

package zone.richardli.datahub.service.strategy;

import zone.richardli.datahub.model.common.FieldDefinition;

import java.util.Map;

public interface IMappingRule {
    static InheritMapping inheritMapping = new InheritMapping();
    static RegexMapping regexMapping = new RegexMapping();
    static ExpressionMapping expressionMapping = new ExpressionMapping();

    static IMappingRule getRule(FieldDefinition rule) {
        if (rule.getInherit() != null) {
            return inheritMapping;
        } else if (rule.getExpression() != null) {
            return regexMapping;
        } else if (rule.getCapturingRegex() != null) {
            return expressionMapping;
        } else {
            throw new RuntimeException("Unsupported mapping rule.");
        }
    }

    Object apply(Map<String, Object> source, FieldDefinition rule);

}

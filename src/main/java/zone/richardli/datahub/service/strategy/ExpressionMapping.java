package zone.richardli.datahub.service.strategy;

import zone.richardli.datahub.model.common.FieldDefinition;

import java.util.List;
import java.util.Map;

public class ExpressionMapping implements IMappingRule {
    @Override
    public Object apply(Map<String, Object> source, FieldDefinition rule) {
        String expression = rule.getExpression();
        List<String> variables = rule.getVariables();
        return compute(source, expression, variables);
    }

    private Object compute(Map<String, Object> source, String expression, List<String> variables) {
        // TODO
        return null;
    }
}

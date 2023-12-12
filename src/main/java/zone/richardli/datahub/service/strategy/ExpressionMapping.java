package zone.richardli.datahub.service.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import zone.richardli.datahub.model.common.FieldDefinition;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ExpressionMapping implements IMappingRule {
    @Override
    public Object apply(Map<String, Object> source, FieldDefinition rule) {
        String expression = rule.getExpression();

        Map<String, Object> variableMap = new HashMap<>();
        rule.getVariables().forEach((k, v) -> variableMap.put(k, source.get(v)));

        return compute(variableMap, expression);
    }

    protected Object compute(Map<String, Object> source, String expression) {
        expression = expression.replaceAll("(\\$\\{\\d+\\})", "#root['$1']");

        log.info("Expression is {}", expression);

        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(expression);

        return exp.getValue(source);
    }
}

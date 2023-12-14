package zone.richardli.datahub.advise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import zone.richardli.datahub.model.log.Log;
import zone.richardli.datahub.service.LogService;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.function.Consumer;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OperationAspect {

    private final LogService logService;

    @Pointcut("@annotation(zone.richardli.datahub.model.log.Loggable)")
    public void logPointcut(){
    }

    private Consumer<Field> consume(StringBuilder sb, Object result) {
        return field -> {
            try {
                if (field.get(result) != null) {
                    sb.append(field.getName());
                    sb.append(": ");
                    sb.append(field.get(result));
                }
            } catch (IllegalAccessException e) {
                log.error("Failed to get field value", e);
            }
        };
    }

    // TODO: fix the void returning type
    @AfterReturning(pointcut = "logPointcut()")
    public void logAllMethodCallsAdvice(JoinPoint jp){
        String operation = jp.getSignature().getName();
        OffsetDateTime time = OffsetDateTime.now();

        StringBuilder sb = new StringBuilder();
        if (operation.startsWith("update") || operation.startsWith("save")) {
            Object CRUDVo = jp.getArgs()[0];

            Arrays.stream(CRUDVo.getClass().getDeclaredFields())
                    .filter(item -> item.getName().endsWith("id") || item.getName().endsWith("Id"))
                    .forEach(consume(sb, CRUDVo));
        }

        String remark = sb.toString();

        Log log = new Log();
        log.setOperation(operation);
        // log.setOperator("TBD");
        log.setTime(time);
        log.setRemarks(remark.isEmpty() ? "No information provided." : remark);

        logService.saveLog(log);
    }

}

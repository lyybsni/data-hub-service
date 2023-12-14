package zone.richardli.datahub.advise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import zone.richardli.datahub.model.ingest.DataIngestDTO;
import zone.richardli.datahub.model.ingest.DataIngestVO;
import zone.richardli.datahub.model.log.Log;
import zone.richardli.datahub.model.log.LogStatus;
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
    public void logPointcut(){ }

    private Consumer<Field> consume(StringBuilder sb, Object result) {
        return field -> {
            try {
                field.setAccessible(true);
                if (field.get(result) != null) {
                    sb.append(field.getName());
                    sb.append(": ");
                    sb.append(field.get(result));
                }
            } catch (IllegalAccessException e) {
                log.error("Failed to get field value", e);
            } finally {
                field.setAccessible(false);
            }
        };
    }

    @AfterReturning(pointcut = "logPointcut() && execution(void zone.richardli.datahub.service.*.*(..))")
    public void logAllMethodCallsAdviceVoid(JoinPoint jp) {
        logAllMethodCallsAdvice(jp, "");
    }

    @AfterReturning(pointcut = "logPointcut() && execution(String zone.richardli.datahub.service.*.*(..))", returning = "result")
    public void logAllMethodCallsAdvice(JoinPoint jp, String result){
        String operation = jp.getSignature().getName();
        OffsetDateTime time = OffsetDateTime.now();

        StringBuilder sb = new StringBuilder();
        if (operation.startsWith("update") || operation.startsWith("save")) {
            Object CRUDVo = jp.getArgs()[0];

            Arrays.stream(CRUDVo.getClass().getDeclaredFields())
                    .peek(i -> log.info(i.getName()))
                    .filter(item -> item.getName().endsWith("id") || item.getName().endsWith("Id"))
                    .forEach(consume(sb, CRUDVo));
        }

        if (sb.length() > 0) {
            sb.append(";\t");
        }

        if (!result.isEmpty()) {
            sb.append("Created: ");
            sb.append(result);
        }

        String remark = sb.toString();

        Log log = new Log();
        log.setOperation(operation);
        // log.setOperator("TBD");
        log.setTime(time);
        log.setRemarks(remark.isEmpty() ? "No information provided." : remark);
        log.setStatus(LogStatus.SUCCEEDED);

        logService.saveLog(log);
    }

    @AfterReturning(pointcut = "logPointcut() && execution(zone.richardli.datahub.model.ingest.DataIngestDTO " +
            "zone.richardli.datahub.service.SparkService.*(zone.richardli.datahub.model.ingest.DataIngestVO))",
             returning = "result")
    public void logIngestCallsAdvice(JoinPoint jp, DataIngestDTO result) {
        Log log = new Log();
        log.setOperation(jp.getSignature().getName());
        log.setStatus(LogStatus.SUCCEEDED);
        log.setTime(OffsetDateTime.now());

        DataIngestVO input = (DataIngestVO) jp.getArgs()[0];
        log.setRemarks(String.format("ClientId: %s, Collection: %s, Affected rows: %d",
                input.getClientId(),
                result.getTargetCollectionName(),
                result.getAffectedRows()));

        logService.saveLog(log);

    }

    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void logAllMethodCallsAdviceThrowing(JoinPoint jp, Throwable e){
        String operation = jp.getSignature().getName();
        OffsetDateTime time = OffsetDateTime.now();

        StringBuilder sb = new StringBuilder();
        if (operation.startsWith("update") || operation.startsWith("save")) {
            Object CRUDVo = jp.getArgs()[0];

            Arrays.stream(CRUDVo.getClass().getDeclaredFields())
                    .peek(i -> log.info(i.getName()))
                    .filter(item -> item.getName().endsWith("id") || item.getName().endsWith("Id"))
                    .forEach(consume(sb, CRUDVo));

            sb.append("\n");
        }
        sb.append("Exception - ");
        sb.append(e.toString());

        String remark = sb.toString();

        Log log = new Log();
        log.setOperation(operation);
        // log.setOperator("TBD");
        log.setTime(time);
        log.setRemarks(remark);
        log.setStatus(LogStatus.FAILED);

        logService.saveLog(log);
    }

}

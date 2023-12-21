package zone.richardli.datahub.utility;

import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;
import java.util.UUID;

@UtilityClass
public class IdUtils {

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public static String generateClientId() {
        return String.valueOf(OffsetDateTime.now().toInstant().getEpochSecond() - 1540000000);
    }

}

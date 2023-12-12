package zone.richardli.datahub.utility;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class IdUtil {

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

}

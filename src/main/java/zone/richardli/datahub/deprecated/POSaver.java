package zone.richardli.datahub.deprecated;

import java.util.List;

public interface POSaver<T> {

    boolean batchInsertOrUpdate(List<T> POs);

    List<T> batchRead();


}

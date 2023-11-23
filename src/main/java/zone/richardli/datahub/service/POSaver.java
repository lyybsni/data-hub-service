package zone.richardli.datahub.service;

import java.util.List;

public interface POSaver<T> {

    boolean batchInsertOrUpdate(List<T> POs);

    List<T> batchRead();


}

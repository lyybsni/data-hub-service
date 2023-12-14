package zone.richardli.datahub.service;

import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zone.richardli.datahub.model.log.Log;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final Datastore datastore;

    public void saveLog(Log log) {
        datastore.save(log);
    }

    public List<Log> readLogs() {
        return datastore.find(Log.class)
                .iterator(new FindOptions().sort(Sort.descending("time")))
                .toList();
    }

}

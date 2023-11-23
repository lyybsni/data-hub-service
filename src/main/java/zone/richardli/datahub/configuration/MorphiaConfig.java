package zone.richardli.datahub.configuration;

import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MorphiaConfig {

    @Value("${spring.data.mongodb.database:data-hub}")
    private String database;

    @Bean
    public Datastore datastore() {
        Datastore datastore = Morphia.createDatastore(MongoClients.create(), database);
        datastore.getMapper().mapPackage("zone.richardli.datahub.model");
        datastore.ensureIndexes();
        return datastore;
    }

}

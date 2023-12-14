package zone.richardli.datahub.configuration;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MorphiaConfig {

    @Value("${spring.data.mongodb.database:data-hub}")
    private String database;

    @PostConstruct
    void init() {

    }

    @Bean
    public Datastore datastore() {
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(new OffsetDateTimeCodec())
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(codecRegistry)
                .build();

        MongoClient mongoClient = MongoClients.create(settings);

        Datastore datastore = Morphia.createDatastore(mongoClient, database);
        datastore.getMapper().mapPackage("zone.richardli.datahub.model");
        datastore.ensureIndexes();
        return datastore;
    }

}

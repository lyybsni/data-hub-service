package zone.richardli.datahub.service;

import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.MergeOptions;
import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import zone.richardli.datahub.advise.exceptions.InvalidInputException;
import zone.richardli.datahub.model.ingest.DataIngestDTO;
import zone.richardli.datahub.model.ingest.DataIngestVO;
import zone.richardli.datahub.model.log.Loggable;
import zone.richardli.datahub.model.schema.mapping.SchemaMappingPO;
import zone.richardli.datahub.utility.JSONUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SparkService {

    private final Datastore datastore;

    private final JavaSparkContext context;

    private final MongoClient mongoClient;

    @Loggable
    public DataIngestDTO write(DataIngestVO vo) {
        // find one
        String mappingId = vo.getMappingId();
        SchemaMappingPO mapping = datastore.find(SchemaMappingPO.class)
                .filter(Filters.eq("_id", mappingId))
                .first();

        // TODO: should change to a more readable way
        // TODO: change the collection name with randomized identifiers
        String targetCollectionName = (String) JSONUtils.parseJSONTree(mapping.getSchema().getSchema()).get("[0].name");
        log.info("Writing into {}", targetCollectionName);

        SparkSession spark = SparkSession.builder()
                .sparkContext(context.sc())
                .getOrCreate();

        // Since Mongo Morphia and Mongo Client will require Java POJO
        // We use Spark to directly write
        JavaRDD<String> data = context.parallelize(Arrays.stream(vo.getData()).map(d -> {
            try {
                return convertObject(d, mapping);
            } catch (JSONException e) {
                throw new InvalidInputException("The input is not valid.", e);
            }
        }).collect(Collectors.toList()));

        spark.read().json(spark.createDataset(data.rdd(), Encoders.STRING()))
                .write()
                .format("mongodb")
                .option("collection", targetCollectionName)
                .option("database", "data-hub")
                .mode("append")
                .save();

        DataIngestDTO dto = new DataIngestDTO();
        dto.setTargetCollectionName(targetCollectionName);
        dto.setAffectedRows(vo.getData().length);
        return dto;
    }

    public int read() {
        return 0;
    }

    public void merge(String fromCollection, String toCollection, List<String> identifiers) {

        /* TODO: following are to changed */
        log.info("AAAA");
        fromCollection = "programmeCode";
        toCollection = "programmeCode-bak";
        identifiers = List.of("programmeCode");

        // Use spark streaming to merge the data
        MongoCollection<Document> from = mongoClient
                .getDatabase("data-hub")
                .getCollection(fromCollection);

        MongoCollection<Document> to = mongoClient
                .getDatabase("data-hub")
                .getCollection(toCollection);
        try {
            Document indexDocument = new Document();
            identifiers.forEach(i -> indexDocument.append(i, 1));

            from.createIndex(indexDocument, new IndexOptions().unique(true));
            to.createIndex(indexDocument, new IndexOptions().unique(true));

            from.aggregate(List.of(Aggregates.merge(toCollection,
                    new MergeOptions()
                            .uniqueIdentifier(identifiers)   // TODO change
                            .whenMatched(MergeOptions.WhenMatched.REPLACE)
                            .whenNotMatched(MergeOptions.WhenNotMatched.INSERT))))
                    .toCollection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("BBBB");

    }

    private String convertObject(Object data, SchemaMappingPO po) throws JSONException {
        JSONObject jsonObject = JSONUtils.mapToJSONObject(
                JSONUtils.parseJSONTree(new Gson().toJson(data)),
                po.getMapping());

        return jsonObject.get((String) jsonObject.names().get(0)).toString();
    }

}

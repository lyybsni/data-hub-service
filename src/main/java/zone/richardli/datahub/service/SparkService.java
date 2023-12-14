package zone.richardli.datahub.service;

import com.google.gson.Gson;
import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.json.JSONException;
import org.springframework.stereotype.Service;
import zone.richardli.datahub.advise.exceptions.InvalidInputException;
import zone.richardli.datahub.model.ingest.DataIngestDTO;
import zone.richardli.datahub.model.ingest.DataIngestVO;
import zone.richardli.datahub.model.log.Loggable;
import zone.richardli.datahub.model.schema.mapping.SchemaMappingPO;
import zone.richardli.datahub.utility.JSONUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SparkService {

    private final Datastore datastore;

    private final JavaSparkContext context;

    // TODO: use spark to write

    @Loggable
    public DataIngestDTO write(DataIngestVO vo) {
        // find one
        String mappingId = vo.getMappingId();
        SchemaMappingPO mapping = datastore.find(SchemaMappingPO.class)
                .filter(Filters.eq("_id", mappingId))
                .first();

        // TODO: should change to a more readable way
        String targetCollectionName = (String) JSONUtils.parseJSONTree(mapping.getSchema().getSchema()).get("[0].name");
        log.info("Writing into {}", targetCollectionName);

        SparkSession spark = SparkSession.builder()
                .sparkContext(context.sc())
                .getOrCreate();

        // define a streaming query
        // TODO: https://www.mongodb.com/docs/spark-connector/current/streaming-mode/streaming-write/
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

    private String convertObject(Object data, SchemaMappingPO po) throws JSONException {
        return JSONUtils.mapToJSONObject(
                JSONUtils.parseJSONTree(new Gson().toJson(data)),
                po.getMapping()).toString();
    }

}

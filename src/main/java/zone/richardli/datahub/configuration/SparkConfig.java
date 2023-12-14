package zone.richardli.datahub.configuration;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Generated from GPT-4
 */
@Configuration
public class SparkConfig {

    /*
     spark.mongodb.write.connection.uri=mongodb://127.0.0.1/
     spark.mongodb.write.database=myDB
     spark.mongodb.write.collection=myCollection
     spark.mongodb.write.convertJson=any
     */
    @Bean
    public SparkConf sparkConf() {
        return new SparkConf()
                .setAppName("MySparkApp")
                .setMaster("local[*]")
                .set("spark.mongodb.write.connection.uri", "mongodb://127.0.0.1/")
                .set("spark.mongodb.write.database", "data-hub");
    }

    @Bean
    public JavaSparkContext javaSparkContext() {
        return new JavaSparkContext(sparkConf());
    }

}

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

    @Bean
    public SparkConf sparkConf() {
        return new SparkConf()
                .setAppName("MySparkApp")
                .setMaster("local[*]");
    }

    @Bean
    public JavaSparkContext javaSparkContext() {
        return new JavaSparkContext(sparkConf());
    }

}

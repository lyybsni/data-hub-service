package zone.richardli.datahub.controller;

import lombok.AllArgsConstructor;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zone.richardli.datahub.example.ReadSpark;

@AllArgsConstructor
@RestController
@RequestMapping("/spark")
public class SparkExample {

    private final ReadSpark readSpark;

    private final JavaSparkContext javaSparkContext;

    @GetMapping("/")
    public void execute() throws InterruptedException {
        readSpark.execute(javaSparkContext);
    };
}
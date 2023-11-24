package zone.richardli.datahub.controller;

import com.google.gson.Gson;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zone.richardli.datahub.model.common.JSONDataInput;
import zone.richardli.datahub.service.ApplicantSparkService;
import zone.richardli.datahub.task.CSVReader;

import java.io.IOException;
import java.util.List;

/**
 * This controller exposes endpoints for data-inputs and outputs
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/data-ingest")
public class DataIngestController {

    private final ApplicantSparkService applicantSparkService;

    private final CSVReader csvReader;

    /**
     * RESTful interface for directly sending data with well-defined schema.
     * @param input the JSON input with data and schema definition
     * @param target the target document to be written
     */
    @PostMapping("/raw")
    void dataInput(@RequestBody JSONDataInput input, @Param("target") String target) {
        applicantSparkService.mockedSparkBuilder(input.getSchema(), input.getData(), target);
    }


    /**
     * Upload the data directly
     */
    @PostMapping(value = "/raw-file", consumes = {"*/*"})
    void dataBulkInput(@RequestParam("file") MultipartFile file,
                       @RequestParam("schema") MultipartFile schema,
                       @Param("target") String target) {
        JSONDataInput input = new JSONDataInput();
        try {
            input.setData(csvReader.readFileCSV(file).toArray());
            input.setSchema(new Gson().fromJson(csvReader.readFile(schema), Object.class));

            log.info("{}", input);
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }

        applicantSparkService.mockedSparkBuilder(input.getSchema(), input.getData(), target);
    }

    /**
     * Given an output schema, map the data into the desired shape
     */
    @PostMapping("/read")
    List<Object> dataOutput(@RequestBody JSONDataInput input, @Param("target") String target) {
        return applicantSparkService.mockedSparkReader(input.getSchema(), target);
    }

}

package zone.richardli.datahub.controller;

import com.google.gson.Gson;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zone.richardli.datahub.model.common.JSONDataInput;
import zone.richardli.datahub.model.ingest.DataIngestDTO;
import zone.richardli.datahub.model.ingest.DataIngestVO;
import zone.richardli.datahub.service.MockApplicantSparkService;
import zone.richardli.datahub.service.SparkService;
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

    private final MockApplicantSparkService mockApplicantSparkService;

    private final SparkService sparkService;

    private final CSVReader csvReader;

    /**
     * RESTful interface for directly sending data with well-defined schema.
     * @param input the JSON input with data and schema definition
     * @param target the target document to be written
     */
    @PostMapping("/raw")
    @Deprecated
    void dataInput(@RequestBody JSONDataInput input, @Param("target") String target) {
        mockApplicantSparkService.mockedSparkBuilder(input.getSchema(), input.getData(), target);
    }

    @PostMapping("/raw/{id}")
    DataIngestDTO dataInput(@PathVariable("id") String mappingId,
                            @RequestParam("clientId") String clientId,
                            @RequestBody DataIngestVO vo) {
        vo.setMappingId(mappingId);
        vo.setClientId(clientId);
        return sparkService.write(vo);
    }

    /**
     * Upload the data directly
     */
    @Deprecated
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

        mockApplicantSparkService.mockedSparkBuilder(input.getSchema(), input.getData(), target);
    }

    @PostMapping("/raw-file/{id}")
    DataIngestDTO dataBulkInput(@RequestParam("file") MultipartFile file,
                       @RequestParam("clientId") String clientId,
                       @PathVariable("id") String mappingId) throws IOException, CsvException {
        DataIngestVO vo = new DataIngestVO();
        vo.setMappingId(mappingId);
        vo.setClientId(clientId);
        vo.setData(csvReader.readFileCSV(file).toArray());
        return sparkService.write(vo);
    }

    /**
     * Given an output schema, map the data into the desired shape
     */
    @Deprecated
    @PostMapping("/read")
    List<Object> dataOutput(@RequestBody JSONDataInput input, @Param("target") String target) {
        return mockApplicantSparkService.mockedSparkReader(input.getSchema(), target);
    }

}

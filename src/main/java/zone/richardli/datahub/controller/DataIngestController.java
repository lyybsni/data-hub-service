package zone.richardli.datahub.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zone.richardli.datahub.model.common.JSONDataInput;
import zone.richardli.datahub.service.ApplicantSparkService;

/**
 * This controller exposes endpoints for data-inputs and outputs
 */
@Slf4j
@RestController
@RequestMapping("/data-ingest")
public class DataIngestController {

    private static final Gson gson = new Gson();

    @Autowired
    private ApplicantSparkService applicantSparkService;

    /**
     * RESTful interface for directly sending data with well-defined schema.
     * @param input the JSON input with data and schema definition
     * @param target the target document to be written
     */
    @PostMapping("/raw")
    void dataInput(@RequestBody JSONDataInput input, @Param("target") String target) {
        applicantSparkService.mockedSparkBuilder(input.getSchema(), input.getData(), target);
    }

    void dataBulkInput() {

    }

    void dataOutput() {

    }

}

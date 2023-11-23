package zone.richardli.datahub.task;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class CSVReader {

    private static final Gson gson = new Gson();

    public String readFile(MultipartFile file) throws IOException {
        return new String(file.getBytes());
    }

    public List<Object> readFileCSV(MultipartFile file) throws IOException, CsvException {
        com.opencsv.CSVReader reader = new com.opencsv.CSVReader(new InputStreamReader(file.getInputStream()));
        List<String[]> contents = reader.readAll();

        List<Object> result = new ArrayList<>();

        for (int i = 1; i < contents.size(); i++) {
            result.add(convertPlainObject(contents.get(0), contents.get(i)));
        }
        return result;
    }

    private Object convertPlainObject(String[] header, Object[] fields) {
        JsonElement element = gson.toJsonTree(new HashMap<>());
        JsonObject object = element.getAsJsonObject();

        for (int i = 0; i < header.length; i++) {
            object.add(header[i], gson.toJsonTree(gson.toJson(fields[i])));
        }

        return gson.fromJson(element, Object.class);
    }

}

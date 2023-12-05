## Data Hub Service Demonstration

### Upstream Services
- Front-end Schema Mapping Demonstration
- Back-end Service for Data Input

### Downstream Services
- Front-end Service for Data Extraction

### Project Structure
```
Controller
Service
Model
Task
Utility
```

### Current Dependencies
```
Java 11
Spark       // for streaming and data processing
MongoDB     // for database and data warehouse
Kafka       // for message listening
Quartz      // for performing timed tasks
OpenCSV     // for reading the CSVs
```

### Functionalities
#### Data Input

**Raw input.** Given a schema and list of data entities, the system will take in the data for saving and/or updating.

```http request
POST http://data-hub-service:10086/data-ingest/raw?target=#{target}
Content-Type: application/json

{
  "schema": {...},
  "data": {...}
}
```


**File input.** Given a schema and a file (preferably a CSV file), the system will take in the data for saving and/or updating.
```http request
POST http://data-hub-service:10086/data-ingest/raw-file?target=#{target}
Content-Type: multipart/form-data
```

#### Data Output

**Raw output.** Similarly, given a schema mapping, bulk output the records in the systems.

```http request
POST http://data-hub-service:10086/data-ingest/read?target=#{target}
Content-Type: application/json

{
  "schema": {...}
}
```

#### Internal: Data Conversion
Define protocols for front-end and back-end to communicate, support basic augmentations. The schema should comply with JSON format, supporting recursive structures and arrays.

Intentional Support:
- Regex Conversion
- Field Computation
- Expression Computation

### Expandable Points - Musts
```
Blockchain Log
```

### Expandable Points - Enhancements
```
Actuator
Metric
```

### Notes

- Using Spark can help to address NoSQL problem that input document shall not follow the schema required in Java ORM.
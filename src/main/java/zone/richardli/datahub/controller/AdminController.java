package zone.richardli.datahub.controller;

import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zone.richardli.datahub.model.log.Log;
import zone.richardli.datahub.model.schema.*;
import zone.richardli.datahub.model.schema.mapping.SchemaMappingPO;
import zone.richardli.datahub.model.schema.mapping.SchemaMappingVO;
import zone.richardli.datahub.model.schema.schema.SchemaPO;
import zone.richardli.datahub.model.schema.schema.SchemaVO;
import zone.richardli.datahub.model.schema.trail.ConversionTrailRun;
import zone.richardli.datahub.service.AdminService;
import zone.richardli.datahub.service.LogService;
import zone.richardli.datahub.utility.JSONUtils;

import java.io.IOException;
import java.util.List;

/**
 * This class defines controllers for functionalities that a role Super Admin can do,
 * including viewing members and current cron tasks
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    private final LogService logService;

    void viewUserList() {

    }

    void assignPrivileges() {

    }

    @GetMapping("/history")
    List<Log> history() {
        return logService.readLogs();
    }

    @PostMapping("/schema")
    String saveSchema(@RequestBody SchemaVO schemaVO) {
        return adminService.saveSchema(schemaVO);
    }

    @PutMapping("/schema")
    void updateSchema(@RequestBody SchemaVO schemaVO) {
        adminService.updateSchema(schemaVO);
    }

    @GetMapping("/schemas")
    List<SchemaPO> getSchemas() {
        return adminService.getSchemas();
    }

    @GetMapping("/schema/{id}")
    SchemaPO getMapping(@PathVariable("id") String id) {
        List<SchemaPO> result = adminService.getSchema(id);
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Not found");
        }

        return result.get(0);
    }

    @PutMapping ("/mapping/{id}")
    void updateMapping(@PathVariable("id") String id, @RequestBody SchemaMappingVO vo) {
        this.adminService.updateSchemaMapping(id, vo);
    }

    @PostMapping("/mapping")
    String saveSchemaMapping(@RequestBody SchemaMappingVO vo) {
        return this.adminService.saveSchemaMapping(vo);
    }

    @GetMapping("/mapping/{id}")
    SchemaMappingPO getSchemaMapping(@PathVariable("id") String id) {
        List<SchemaMappingPO> result = this.adminService.getSchemaMapping(id);
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Not found");
        }
        return result.get(0);
    }

    @GetMapping("/history/{id}")
    List<SchemaMappingPO> getExistingSchemaMappingUnder(@PathVariable("id") String id) {
        return adminService.getRelatedSchemaMapping(id);
    }

    @PostMapping("/schema-from-csv")
    ResolveSchemaDataDTO resolveSchemaFromCSV(@RequestParam MultipartFile file) {
        return adminService.resolveCSVSchema(file);
    }

    @PostMapping("/schema-from-json")
    ResolveSchemaDataDTO resolveSchemaFromJSON(@RequestParam MultipartFile file) throws IOException {
        return adminService.resolveJSONSchema(file);
    }

    @PostMapping("/try")
    Object trail(@RequestBody ConversionTrailRun vo) throws JSONException {
        SchemaMappingPO po = adminService.getSchemaMapping(vo.getMappingId()).get(0);
        return JSONUtils.constructObject(JSONUtils.parseJSONTree(vo.getJson()), po.getMapping(), Object.class);
    }

}

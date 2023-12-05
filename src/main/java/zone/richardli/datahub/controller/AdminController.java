package zone.richardli.datahub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import zone.richardli.datahub.model.schema.SchemaMappingPO;
import zone.richardli.datahub.model.schema.SchemaMappingVO;
import zone.richardli.datahub.model.schema.SchemaPO;
import zone.richardli.datahub.model.schema.SchemaVO;
import zone.richardli.datahub.service.AdminService;

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

    void viewUserList() {

    }

    void assignPrivileges() {

    }


    @PostMapping("/upload-schema")
    String saveMapping(@RequestBody SchemaVO schemaVO) {
        return adminService.saveSchema(schemaVO);
    }

    @GetMapping("/schema/{id}")
    SchemaPO getMapping(@PathVariable("id") String id) {
        List<SchemaPO> result = adminService.getSchema(id);
        if (result.isEmpty()) {
            throw new IllegalArgumentException("Not found");
        }

        return result.get(0);
    }

    @GetMapping("/schemas")
    List<SchemaPO> getSchemas() {
        return adminService.getSchemas();
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

}

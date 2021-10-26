package org.example.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.model.ATMCase;
import org.example.service.ATMCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest")
public class ATMCaseController {

    @Autowired
    private ATMCaseService service;

    @PostMapping("/file")
    public Integer readFile(@RequestBody List<JsonNode> entities) {
        return service.readFile(entities);
    }

    @GetMapping("/")
    public List<ATMCase> getAllCases() {
        return service.getAllCases();
    }

    @DeleteMapping("/")
    public Integer deleteAll() {
        return service.deleteAll();
    }

    @GetMapping("/top-causes")
    public Map<String, List<ATMCase>> getTopThreeCauses() {
        return service.getTopThreeCauses();
    }

    @GetMapping("/top-length")
    public List<ATMCase> getTopThreeLength() {
        return service.getTopThreeLength();
    }

    @GetMapping("/duplicates")
    public Map<String, List<ATMCase>> getDuplicates() {
        return service.getDuplicates();
    }
}

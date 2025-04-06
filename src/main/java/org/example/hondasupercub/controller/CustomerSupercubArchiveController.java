package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.ModelArchiveDTO;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.service.CustomerSupercubArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/archive")
public class CustomerSupercubArchiveController {

    private final CustomerSupercubArchiveService archiveService;

    @Autowired
    public CustomerSupercubArchiveController(CustomerSupercubArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    @GetMapping("/models")
    public ResponseEntity<ResponseDTO> getAllModels() {
        List<ModelArchiveDTO> models = archiveService.getAllModels();
        ResponseDTO responseDTO = new ResponseDTO(200, "Models retrieved successfully.", models);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/models/{id}")
    public ResponseEntity<ResponseDTO> getModelById(@PathVariable int id) {
        ModelArchiveDTO model = archiveService.getModelById(id);
        if (model != null) {
            ResponseDTO responseDTO = new ResponseDTO(200, "Model retrieved successfully.", model);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            ResponseDTO responseDTO = new ResponseDTO(404, "Model not found.", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/models/search")
    public ResponseEntity<ResponseDTO> searchModels(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String modelName,
            @RequestParam(required = false) String vinNumber
    ) {
        System.out.println("Received year: " + year);
        System.out.println("Received modelName: " + modelName);
        System.out.println("Received vinNumber: " + vinNumber);

        List<ModelArchiveDTO> results = archiveService.searchModels(year, modelName, vinNumber);

        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK.value(), "Models retrieved successfully", results);
        return ResponseEntity.ok(responseDTO);
    }
}
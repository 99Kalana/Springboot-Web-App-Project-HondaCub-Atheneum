package org.example.hondasupercub.controller;


import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.SparePartDTO;
import org.example.hondasupercub.service.impl.AdminSparePartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/adminspareparts")
public class AdminSparePartController {

    @Autowired
    private AdminSparePartServiceImpl sparePartService;

    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAllSpareParts() {
        List<SparePartDTO> spareParts = sparePartService.getAllSparePartsDTO(); // Assuming you have a method for DTOs
        ResponseDTO responseDTO = new ResponseDTO(200, "Spare Parts List", spareParts);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseDTO> getSparePartById(@PathVariable Integer id) {
        SparePartDTO sparePart = sparePartService.getSparePartDTOById(id); // Assuming you have a method for DTO by ID
        if (sparePart != null) {
            ResponseDTO responseDTO = new ResponseDTO(200, "Spare Part Found", sparePart);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            ResponseDTO responseDTO = new ResponseDTO(404, "Spare Part Not Found", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchSpareParts(@RequestParam String query) {
        List<SparePartDTO> spareParts = sparePartService.searchSparePartsDTO(query); // Assuming you have a method for searching DTO
        ResponseDTO responseDTO = new ResponseDTO(200, "Spare Parts Search Results", spareParts);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<ResponseDTO> filterSparePartsByCategory(@RequestParam String category) {
        List<SparePartDTO> spareParts = sparePartService.filterSparePartsByCategoryDTO(category); // Assuming you have a method for filtering DTO
        ResponseDTO responseDTO = new ResponseDTO(200, "Spare Parts Filtered Results", spareParts);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
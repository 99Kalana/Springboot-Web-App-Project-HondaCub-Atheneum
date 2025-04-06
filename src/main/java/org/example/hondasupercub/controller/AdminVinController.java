package org.example.hondasupercub.controller;

import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.dto.VinHistoryDTO;
import org.example.hondasupercub.dto.VinPartsDTO;
import org.example.hondasupercub.service.AdminVinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/adminvin")
public class AdminVinController {

    @Autowired
    private AdminVinService vinService;

    @PostMapping("/save")
    public ResponseEntity<ResponseDTO> saveVinHistory(@RequestBody VinHistoryDTO vinHistoryDTO) {
        VinHistoryDTO savedVinHistory = vinService.saveVinHistory(vinHistoryDTO);
        ResponseDTO responseDTO = new ResponseDTO(201, "VIN History Saved", savedVinHistory);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/get/{vinId}")
    public ResponseEntity<ResponseDTO> getVinHistoryById(@PathVariable int vinId) {
        VinHistoryDTO vinHistory = vinService.getVinHistoryById(vinId);
        if (vinHistory != null) {
            ResponseDTO responseDTO = new ResponseDTO(200, "VIN History Found", vinHistory);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            ResponseDTO responseDTO = new ResponseDTO(404, "VIN History Not Found", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAllVinHistories() {
        List<VinHistoryDTO> vinHistories = vinService.getAllVinHistories();
        ResponseDTO responseDTO = new ResponseDTO(200, "VIN Histories List", vinHistories);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{vinId}")
    public ResponseEntity<ResponseDTO> deleteVinHistory(@PathVariable int vinId) {
        vinService.deleteVinHistory(vinId);
        ResponseDTO responseDTO = new ResponseDTO(200, "VIN History Deleted", null);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PostMapping("/linkPart")
    public ResponseEntity<ResponseDTO> linkVinToPart(@RequestBody VinPartsDTO vinPartsDTO) {
        VinPartsDTO linkedPart = vinService.linkVinToPart(vinPartsDTO);
        if (linkedPart != null) {
            ResponseDTO responseDTO = new ResponseDTO(201, "Part Linked to VIN", linkedPart);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } else {
            ResponseDTO responseDTO = new ResponseDTO(400, "Failed to Link Part to VIN", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }
    }


}
package org.example.hondasupercub.controller;

import jakarta.validation.Valid;
import org.example.hondasupercub.dto.ModelArchiveDTO;
import org.example.hondasupercub.dto.ModelImageDTO;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.entity.ModelImage;
import org.example.hondasupercub.repo.ModelImageRepo;
import org.example.hondasupercub.service.AdminArchiveService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/adminarchive")
public class AdminArchiveController {

    @Autowired
    private AdminArchiveService archiveService;

    @Autowired
    private ModelImageRepo imageRepo;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/save")
    public ResponseEntity<ResponseDTO> saveModelArchive(@Valid @RequestPart("modelArchive") ModelArchiveDTO modelArchiveDTO,
                                                        @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        ModelArchiveDTO savedModel = archiveService.saveModelArchiveDTO(modelArchiveDTO, images);
        ResponseDTO responseDTO = new ResponseDTO(201, "Model Archive Saved", savedModel);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateModelArchive(@Valid @RequestPart("modelArchive") ModelArchiveDTO modelArchiveDTO,
                                                          @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        ModelArchiveDTO updatedModel = archiveService.updateModelArchiveDTO(modelArchiveDTO, images);
        ResponseDTO responseDTO = new ResponseDTO(200, "Model Archive Updated", updatedModel);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseDTO> getModelArchiveById(@PathVariable int id) {
        ModelArchiveDTO modelArchiveDTO = archiveService.getModelArchiveDTOById(id);
        if (modelArchiveDTO == null) {
            return new ResponseEntity<>(new ResponseDTO(404, "Model Archive Not Found", null), HttpStatus.NOT_FOUND);
        }
        ResponseDTO responseDTO = new ResponseDTO(200, "Model Archive Found", modelArchiveDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAllModelArchives() {
        List<ModelArchiveDTO> modelArchiveDTOs = archiveService.getAllModelArchiveDTOs();
        ResponseDTO responseDTO = new ResponseDTO(200, "All Model Archives", modelArchiveDTOs);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDTO> deleteModelArchive(@PathVariable int id) {
        archiveService.deleteModelArchive(id);
        ResponseDTO responseDTO = new ResponseDTO(200, "Model Archive Deleted", null);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/image/{imageId}")
    public ResponseEntity<ResponseDTO> getImageById(@PathVariable int imageId) {
        ModelImage modelImage = imageRepo.findById(imageId).orElse(null);
        if (modelImage == null) {
            return new ResponseEntity<>(new ResponseDTO(404, "Image Not Found", null), HttpStatus.NOT_FOUND);
        }
        ModelImageDTO modelImageDTO = modelMapper.map(modelImage, ModelImageDTO.class);
        ResponseDTO responseDTO = new ResponseDTO(200, "Image Found", modelImageDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchModelArchives(@RequestParam String modelName) {
        List<ModelArchiveDTO> modelArchiveDTOs = archiveService.searchModelArchiveDTOs(modelName);
        ResponseDTO responseDTO = new ResponseDTO(200, "Search Results", modelArchiveDTOs);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}
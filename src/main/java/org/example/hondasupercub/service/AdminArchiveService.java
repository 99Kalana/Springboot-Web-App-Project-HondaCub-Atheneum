package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.ModelArchiveDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminArchiveService {

    ModelArchiveDTO saveModelArchiveDTO(ModelArchiveDTO modelArchiveDTO, List<MultipartFile> images);

    ModelArchiveDTO updateModelArchiveDTO(ModelArchiveDTO modelArchiveDTO, List<MultipartFile> images);

    ModelArchiveDTO getModelArchiveDTOById(int id);

    List<ModelArchiveDTO> getAllModelArchiveDTOs();

    void deleteModelArchive(int id);

    List<ModelArchiveDTO> searchModelArchiveDTOs(String modelName);
}
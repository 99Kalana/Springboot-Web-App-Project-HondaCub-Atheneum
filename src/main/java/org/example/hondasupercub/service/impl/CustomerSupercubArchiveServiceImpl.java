package org.example.hondasupercub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.hondasupercub.dto.ModelArchiveDTO;
import org.example.hondasupercub.entity.ModelArchive;
import org.example.hondasupercub.entity.ModelImage;
import org.example.hondasupercub.entity.VinHistory;
import org.example.hondasupercub.repo.CustomerSupercubArchiveModelImageRepo;
import org.example.hondasupercub.repo.CustomerSupercubArchiveRepo;
import org.example.hondasupercub.repo.CustomerSupercubArchiveVinHistoryRepo;
import org.example.hondasupercub.service.CustomerSupercubArchiveService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CustomerSupercubArchiveServiceImpl implements CustomerSupercubArchiveService {

    private final CustomerSupercubArchiveRepo archiveRepo;
    private final CustomerSupercubArchiveModelImageRepo imageRepo;
    private final CustomerSupercubArchiveVinHistoryRepo vinRepo;
    private final ModelMapper modelMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    public CustomerSupercubArchiveServiceImpl(CustomerSupercubArchiveRepo archiveRepo, CustomerSupercubArchiveModelImageRepo imageRepo, CustomerSupercubArchiveVinHistoryRepo vinRepo, ModelMapper modelMapper) {
        this.archiveRepo = archiveRepo;
        this.imageRepo = imageRepo;
        this.vinRepo = vinRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ModelArchiveDTO> getAllModels() {
        List<ModelArchive> models = archiveRepo.findAll();
        return models.stream()
                .map(model -> archiveRepo.findModelArchiveById(model.getModelId()))
                .map(this::mapModelArchiveToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ModelArchiveDTO getModelById(int id) {
        ModelArchive model = archiveRepo.findModelArchiveById(id);
        if (model == null) {
            return null;
        }
        return mapModelArchiveToDTO(model);
    }

    @Override
    public List<ModelArchiveDTO> searchModels(Integer year, String modelName, String vinNumber) {
        List<ModelArchive> results = archiveRepo.findAll(); // Start with all models

        if (year != null) {
            results.retainAll(archiveRepo.findByModelYear(year)); // Keep only models matching the year
        }

        if (modelName != null && !modelName.isEmpty()) {
            results.retainAll(archiveRepo.findByModelNameContainingIgnoreCase(modelName)); // Keep only models matching the modelName
        }

        if (vinNumber != null && !vinNumber.isEmpty()) {
            List<VinHistory> vinHistories = vinRepo.findByVinNumber(vinNumber);
            List<ModelArchive> vinModels = vinHistories.stream()
                    .filter(vinHistory -> vinHistory.getModelArchive() != null)
                    .map(vinHistory -> archiveRepo.findModelArchiveById(vinHistory.getModelArchive().getModelId()))
                    .collect(Collectors.toList());
            results.retainAll(vinModels); // Keep only models matching the vinNumber
        }

        return results.stream().distinct()
                .map(this::mapModelArchiveToDTO)
                .collect(Collectors.toList());
    }

    private ModelArchiveDTO mapModelArchiveToDTO(ModelArchive model) {
        ModelArchiveDTO dto = new ModelArchiveDTO();
        if (model != null) {
            modelMapper.map(model, dto);

            // Fetch vinHistories and modelImages separately
            List<VinHistory> vinHistories = archiveRepo.findVinHistoriesByModelId(model.getModelId());
            List<ModelImage> modelImages = archiveRepo.findModelImagesByModelId(model.getModelId());

            dto.setModelImageIds(modelImages.stream()
                    .map(ModelImage::getImageId)
                    .collect(Collectors.toList()));

            // In CustomerSupercubArchiveServiceImpl.java, within mapModelArchiveToDTO:
            dto.setModelImages(modelImages.stream()
                    .map(image -> {
                        ModelArchiveDTO.ModelImageDTO imageDTO = new ModelArchiveDTO.ModelImageDTO();
                        imageDTO.setImageId(image.getImageId());
                        // Adjust the URL if necessary:
                        imageDTO.setImageUrl("http://localhost:63342/images/" + image.getImageUrl());
                        return imageDTO;
                    })
                    .collect(Collectors.toList()));

            dto.setVinHistoryIds(vinHistories.stream()
                    .map(VinHistory::getVinId)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private String extractEmailFromToken(String authorizationHeader) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                Claims claims = Jwts.parser().setSigningKey(secretKey)
                        .parseClaimsJws(authorizationHeader.substring(7)).getBody();
                return claims.getSubject();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
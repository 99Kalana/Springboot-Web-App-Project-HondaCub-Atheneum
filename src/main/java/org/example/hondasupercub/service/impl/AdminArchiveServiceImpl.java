package org.example.hondasupercub.service.impl;

import org.example.hondasupercub.dto.ModelArchiveDTO;
import org.example.hondasupercub.dto.ModelImageDTO;
import org.example.hondasupercub.entity.ModelArchive;
import org.example.hondasupercub.entity.ModelImage;
import org.example.hondasupercub.repo.AdminArchiveRepo;
import org.example.hondasupercub.repo.ModelImageRepo;
import org.example.hondasupercub.service.AdminArchiveService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminArchiveServiceImpl implements AdminArchiveService {

    @Autowired
    private AdminArchiveRepo archiveRepo;

    @Autowired
    private ModelImageRepo imageRepo;

    @Autowired
    private ModelMapper modelMapper;

    private final Path imageStorageLocation = Paths.get("src/main/resources/images"); // Directory to store images

    @Override
    public ModelArchiveDTO saveModelArchiveDTO(ModelArchiveDTO modelArchiveDTO, List<MultipartFile> images) {
        ModelArchive modelArchive = modelMapper.map(modelArchiveDTO, ModelArchive.class);
        ModelArchive savedModel = archiveRepo.save(modelArchive);

        List<ModelImage> savedImages = saveImages(savedModel, images);
        savedModel.setModelImages(savedImages); // Set images to the model

        return modelMapper.map(savedModel, ModelArchiveDTO.class);
    }

    private List<ModelImage> saveImages(ModelArchive modelArchive, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }

        List<ModelImage> savedImages = new ArrayList<>();
        images.forEach(image -> {
            try {
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path targetLocation = imageStorageLocation.resolve(fileName);
                Files.copy(image.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                ModelImage modelImage = new ModelImage();
                modelImage.setModelArchive(modelArchive);
                modelImage.setImageUrl(fileName); // Store filename, not full path

                savedImages.add(imageRepo.save(modelImage));
            } catch (IOException e) {
                e.printStackTrace(); // Handle exception
            }
        });
        return savedImages;
    }

    @Override
    public ModelArchiveDTO updateModelArchiveDTO(ModelArchiveDTO modelArchiveDTO, List<MultipartFile> images) {
        if (!archiveRepo.existsById(modelArchiveDTO.getModelId())) {
            throw new RuntimeException("Model archive does not exist");
        }
        ModelArchive modelArchive = modelMapper.map(modelArchiveDTO, ModelArchive.class);
        ModelArchive updatedModel = archiveRepo.save(modelArchive);

        // Delete old images and add new ones
        imageRepo.findByModelArchive_ModelId(updatedModel.getModelId()).forEach(imageRepo::delete);
        updatedModel.setModelImages(saveImages(updatedModel, images));

        return modelMapper.map(updatedModel, ModelArchiveDTO.class);
    }

    @Override
    public ModelArchiveDTO getModelArchiveDTOById(int id) {
        ModelArchive modelArchive = archiveRepo.findById(id).orElse(null);
        if (modelArchive == null) {
            return null;
        }

        ModelArchiveDTO dto = modelMapper.map(modelArchive, ModelArchiveDTO.class);
        List<ModelImage> images = imageRepo.findByModelArchive_ModelId(id);
        dto.setModelImageIds(images.stream().map(ModelImage::getImageId).collect(Collectors.toList()));

        return dto;
    }

    /*@Override
    public List<ModelArchiveDTO> getAllModelArchiveDTOs() {
        List<ModelArchive> models = archiveRepo.findAll();
        return modelMapper.map(models, new TypeToken<List<ModelArchiveDTO>>() {}.getType());
    }*/

    @Override
    public List<ModelArchiveDTO> getAllModelArchiveDTOs() {
        List<ModelArchive> models = archiveRepo.findAll();
        List<ModelArchiveDTO> modelArchiveDTOs = new ArrayList<>();

        for (ModelArchive model : models) {
            ModelArchiveDTO dto = modelMapper.map(model, ModelArchiveDTO.class);
            List<ModelImage> images = imageRepo.findByModelArchive_ModelId(model.getModelId());
            dto.setModelImageIds(images.stream().map(ModelImage::getImageId).collect(Collectors.toList()));
            modelArchiveDTOs.add(dto);
        }
        return modelArchiveDTOs;
    }

    /*@Override
    public void deleteModelArchive(int id) {
        archiveRepo.deleteById(id);
    }*/

    @Override
    @Transactional // Add this annotation
    public void deleteModelArchive(int id) {
        // Delete related images first
        List<ModelImage> images = imageRepo.findByModelArchive_ModelId(id);
        imageRepo.deleteAll(images);

        // Then delete the model archive
        archiveRepo.deleteById(id);
    }

    @Override
    public List<ModelArchiveDTO> searchModelArchiveDTOs(String modelName) {
        List<ModelArchive> models = archiveRepo.findByModelNameContainingIgnoreCase(modelName);
        List<ModelArchiveDTO> modelArchiveDTOs = new ArrayList<>();

        for (ModelArchive model : models) {
            ModelArchiveDTO dto = modelMapper.map(model, ModelArchiveDTO.class);
            List<ModelImage> images = imageRepo.findByModelArchive_ModelId(model.getModelId());
            dto.setModelImageIds(images.stream().map(ModelImage::getImageId).collect(Collectors.toList()));
            modelArchiveDTOs.add(dto);
        }
        return modelArchiveDTOs;
    }

}

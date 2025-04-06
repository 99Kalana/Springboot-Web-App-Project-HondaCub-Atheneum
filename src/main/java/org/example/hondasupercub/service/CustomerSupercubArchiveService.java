package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.ModelArchiveDTO;

import java.util.List;

public interface CustomerSupercubArchiveService {
    List<ModelArchiveDTO> getAllModels();
    ModelArchiveDTO getModelById(int id);
    List<ModelArchiveDTO> searchModels(Integer year, String modelName, String vinNumber);
}
package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.SparePartDTO;

import java.util.List;

public interface AdminSparePartService {

    List<SparePartDTO> getAllSparePartsDTO();

    SparePartDTO getSparePartDTOById(Integer id);


    List<SparePartDTO> searchSparePartsDTO(String query);

    List<SparePartDTO> filterSparePartsByCategoryDTO(String categoryName);
}
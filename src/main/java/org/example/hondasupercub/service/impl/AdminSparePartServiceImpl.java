package org.example.hondasupercub.service.impl;


import org.example.hondasupercub.dto.SparePartDTO;
import org.example.hondasupercub.entity.SparePart; // Assuming you have a SparePart entity
import org.example.hondasupercub.repo.AdminSparePartRepo;
import org.example.hondasupercub.service.AdminSparePartService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminSparePartServiceImpl implements AdminSparePartService {

    @Autowired
    private AdminSparePartRepo sparePartRepo;



    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<SparePartDTO> getAllSparePartsDTO() {
        List<SparePart> spareParts = sparePartRepo.findAll();
        return modelMapper.map(spareParts, new TypeToken<List<SparePartDTO>>() {}.getType());
    }

    @Override
    public SparePartDTO getSparePartDTOById(Integer id) {
        Optional<SparePart> sparePart = sparePartRepo.findById(id);
        return sparePart.map(value -> modelMapper.map(value, SparePartDTO.class)).orElse(null);
    }





    @Override
    public List<SparePartDTO> searchSparePartsDTO(String query) {
        List<SparePart> spareParts = sparePartRepo.findByPartNameContainingIgnoreCase(query);
        return modelMapper.map(spareParts, new TypeToken<List<SparePartDTO>>() {}.getType());
    }

    @Override
    public List<SparePartDTO> filterSparePartsByCategoryDTO(String categoryName) {
        List<SparePart> spareParts = sparePartRepo.findByCategory_CategoryNameIgnoreCase(categoryName);
        return modelMapper.map(spareParts, new TypeToken<List<SparePartDTO>>() {}.getType());
    }
}
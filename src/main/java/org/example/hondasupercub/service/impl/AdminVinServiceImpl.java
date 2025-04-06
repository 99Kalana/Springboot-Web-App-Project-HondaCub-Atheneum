package org.example.hondasupercub.service.impl;

import org.example.hondasupercub.dto.VinHistoryDTO;
import org.example.hondasupercub.dto.VinPartsDTO;
import org.example.hondasupercub.entity.ModelArchive;
import org.example.hondasupercub.entity.SparePart;
import org.example.hondasupercub.entity.VinHistory;
import org.example.hondasupercub.entity.VinParts;
import org.example.hondasupercub.repo.AdminArchiveRepo;
import org.example.hondasupercub.repo.AdminSparePartRepo;
import org.example.hondasupercub.repo.AdminVinHistoryRepo;
import org.example.hondasupercub.repo.AdminVinPartsRepo;
import org.example.hondasupercub.service.AdminVinService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminVinServiceImpl implements AdminVinService {

    @Autowired
    private AdminVinHistoryRepo vinHistoryRepo;

    @Autowired
    private AdminVinPartsRepo vinPartsRepo;

    @Autowired
    private AdminArchiveRepo modelArchiveRepo;

    @Autowired
    private AdminSparePartRepo sparePartRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public VinHistoryDTO saveVinHistory(VinHistoryDTO vinHistoryDTO) {
        ModelArchive modelArchive = modelArchiveRepo.findById(vinHistoryDTO.getModelId()).orElse(null);
        VinHistory vinHistory = modelMapper.map(vinHistoryDTO, VinHistory.class);
        vinHistory.setModelArchive(modelArchive);
        VinHistory savedVinHistory = vinHistoryRepo.save(vinHistory);
        return modelMapper.map(savedVinHistory, VinHistoryDTO.class);
    }

    @Override
    public VinHistoryDTO getVinHistoryById(int vinId) {
        VinHistory vinHistory = vinHistoryRepo.findById(vinId).orElse(null);
        return mapVinHistoryToDTO(vinHistory);
    }

    @Override
    public List<VinHistoryDTO> getAllVinHistories() {
        List<VinHistory> vinHistories = vinHistoryRepo.findAll();
        return vinHistories.stream()
                .map(this::mapVinHistoryToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteVinHistory(int vinId) {
        vinPartsRepo.findByVinHistory_VinId(vinId).forEach(vinPartsRepo::delete);
        vinHistoryRepo.deleteById(vinId);
    }


    @Override
    public VinPartsDTO linkVinToPart(VinPartsDTO vinPartsDTO) {
        VinHistory vinHistory = vinHistoryRepo.findById(vinPartsDTO.getVinId()).orElse(null);
        SparePart sparePart = sparePartRepo.findById(vinPartsDTO.getPartId()).orElse(null);

        if (vinHistory == null || sparePart == null) {
            return null; // Handle error: vinHistory or sparePart not found
        }

        VinParts vinParts = new VinParts();
        vinParts.setVinHistory(vinHistory);
        vinParts.setSparePart(sparePart);
        VinParts savedVinParts = vinPartsRepo.save(vinParts);

        // Correct Mapping
        VinPartsDTO resultDTO = new VinPartsDTO();
        resultDTO.setVinId(savedVinParts.getVinHistory().getVinId());
        resultDTO.setPartId(savedVinParts.getSparePart().getPartId());

        return resultDTO;
    }



    private VinHistoryDTO mapVinHistoryToDTO(VinHistory vinHistory) {
        if (vinHistory == null) {
            return null;
        }
        return modelMapper.map(vinHistory, VinHistoryDTO.class);
    }
}
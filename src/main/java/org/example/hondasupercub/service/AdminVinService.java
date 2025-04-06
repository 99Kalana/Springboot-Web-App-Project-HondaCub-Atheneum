package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.VinHistoryDTO;
import org.example.hondasupercub.dto.VinPartsDTO;

import java.util.List;

public interface AdminVinService {
    VinHistoryDTO saveVinHistory(VinHistoryDTO vinHistoryDTO);
    VinHistoryDTO getVinHistoryById(int vinId);
    List<VinHistoryDTO> getAllVinHistories();
    void deleteVinHistory(int vinId);
    VinPartsDTO linkVinToPart(VinPartsDTO vinPartsDTO);

}
package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.VinHistory;
import org.example.hondasupercub.entity.VinParts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminVinPartsRepo extends JpaRepository<VinParts, Integer> {
    List<VinParts> findByVinHistory_VinId(int vinId);
}

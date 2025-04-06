package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.SparePartImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SparePartImageRepo extends JpaRepository<SparePartImage, Integer> {
    List<SparePartImage> findBySparePart_PartId(int partId);
}

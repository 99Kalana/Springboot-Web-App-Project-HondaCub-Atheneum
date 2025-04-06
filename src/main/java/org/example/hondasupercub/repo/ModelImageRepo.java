package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.ModelImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelImageRepo extends JpaRepository<ModelImage, Integer> {
    List<ModelImage> findByModelArchive_ModelId(int modelId);
}
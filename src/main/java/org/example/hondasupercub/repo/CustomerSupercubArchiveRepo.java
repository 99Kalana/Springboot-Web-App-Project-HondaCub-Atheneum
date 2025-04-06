package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.ModelArchive;
import org.example.hondasupercub.entity.ModelImage;
import org.example.hondasupercub.entity.VinHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerSupercubArchiveRepo extends JpaRepository<ModelArchive, Integer> {
    List<ModelArchive> findByModelYear(int modelYear);
    List<ModelArchive> findByModelNameContainingIgnoreCase(String modelName);

    @Query("SELECT m FROM ModelArchive m WHERE m.modelId = :modelId")
    ModelArchive findModelArchiveById(@Param("modelId") int modelId);

    @Query("SELECT vh FROM VinHistory vh WHERE vh.modelArchive.modelId = :modelId")
    List<VinHistory> findVinHistoriesByModelId(@Param("modelId") int modelId);

    @Query("SELECT mi FROM ModelImage mi WHERE mi.modelArchive.modelId = :modelId")
    List<ModelImage> findModelImagesByModelId(@Param("modelId") int modelId);
}
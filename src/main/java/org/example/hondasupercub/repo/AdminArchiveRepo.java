package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.ModelArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminArchiveRepo extends JpaRepository<ModelArchive, Integer> {
    List<ModelArchive> findByModelNameContainingIgnoreCase(String modelName);
}
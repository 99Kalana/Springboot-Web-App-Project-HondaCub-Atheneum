package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.SparePartImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerProductsSparePartImageRepo extends JpaRepository<SparePartImage, Integer> {
}
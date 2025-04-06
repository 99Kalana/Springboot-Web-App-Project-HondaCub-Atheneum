package org.example.hondasupercub.repo;


import org.example.hondasupercub.entity.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminSparePartRepo extends JpaRepository<SparePart, Integer> {
    List<SparePart> findByPartNameContainingIgnoreCase(String partName);
    List<SparePart> findByCategory_CategoryNameIgnoreCase(String categoryName);
}
package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerHomeSparePartRepo extends JpaRepository<SparePart, Integer> {
}

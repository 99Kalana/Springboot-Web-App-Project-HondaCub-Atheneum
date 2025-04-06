package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SparePartRepo extends JpaRepository<SparePart, Integer> {
    List<SparePart> findBySeller_UserId(int sellerId);

    SparePart findByPartIdAndSeller_UserId(int partId, int sellerId);


    long countBySellerUserId(int sellerId);
}

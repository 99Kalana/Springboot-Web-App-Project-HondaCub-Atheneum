package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SparePartRepo extends JpaRepository<SparePart, Integer> {
    List<SparePart> findBySeller_UserId(int sellerId);

    SparePart findByPartIdAndSeller_UserId(int partId, int sellerId);

    List<SparePart> findBySeller_UserIdAndCategory_CategoryId(int sellerId, int categoryId);

    // Search by part name (case-insensitive) OR part ID for a specific seller
    @Query("SELECT sp FROM SparePart sp WHERE sp.seller.userId = :sellerId AND (LOWER(sp.partName) LIKE %:search% OR CAST(sp.partId AS string) = :search)")
    List<SparePart> findBySeller_UserIdAndPartNameOrPartId(@Param("sellerId") int sellerId, @Param("search") String search);

    // Search by part name/ID AND category for a specific seller
    @Query("SELECT sp FROM SparePart sp WHERE sp.seller.userId = :sellerId AND sp.category.categoryId = :categoryId AND (LOWER(sp.partName) LIKE %:search% OR CAST(sp.partId AS string) = :search)")
    List<SparePart> findBySeller_UserIdAndCategory_CategoryIdAndPartNameOrPartId(
            @Param("sellerId") int sellerId, @Param("categoryId") int categoryId, @Param("search") String search
    );

    long countBySellerUserId(int sellerId);
}
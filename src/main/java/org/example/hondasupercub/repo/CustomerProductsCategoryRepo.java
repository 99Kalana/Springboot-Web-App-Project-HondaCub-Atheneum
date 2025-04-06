package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerProductsCategoryRepo extends JpaRepository<Category, Integer> {
    @Query("SELECT c.categoryName FROM Category c")
    List<String> findAllCategoryNames();
}

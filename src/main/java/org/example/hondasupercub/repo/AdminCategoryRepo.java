package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminCategoryRepo extends JpaRepository<Category, Integer> {
    List<Category> findByCategoryNameContaining(String categoryName);
}

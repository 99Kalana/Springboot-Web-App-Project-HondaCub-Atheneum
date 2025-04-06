package org.example.hondasupercub.service;

import org.example.hondasupercub.dto.CategoryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminCategoryService {
    void addCategory(CategoryDTO categoryDTO);

    List<CategoryDTO> getAllCategories();

    void updateCategory(CategoryDTO categoryDTO);

    void deleteCategory(int id);

    CategoryDTO getCategoryById(int id);

    List<CategoryDTO> searchCategories(String term);
}

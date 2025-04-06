package org.example.hondasupercub.service.impl;

import org.example.hondasupercub.dto.CategoryDTO;
import org.example.hondasupercub.entity.Category;
import org.example.hondasupercub.repo.AdminCategoryRepo;
import org.example.hondasupercub.service.AdminCategoryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminCategoryServiceImpl implements AdminCategoryService {
    @Autowired
    private AdminCategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        categoryRepo.save(modelMapper.map(categoryDTO, Category.class));
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepo.findAll();
        return modelMapper.map(categories, new TypeToken<List<CategoryDTO>>() {}.getType());
    }

    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        if (!categoryRepo.existsById(categoryDTO.getCategoryId())) {
            throw new RuntimeException("Category does not exist");
        }
        categoryRepo.save(modelMapper.map(categoryDTO, Category.class));
    }

    @Override
    public void deleteCategory(int id) {
        categoryRepo.deleteById(id);
    }

    @Override
    public CategoryDTO getCategoryById(int id) {
        Optional<Category> category = categoryRepo.findById(id);
        return category.map(value -> modelMapper.map(value, CategoryDTO.class)).orElse(null);
    }

    @Override
    public List<CategoryDTO> searchCategories(String term) {
        List<Category> categories = categoryRepo.findByCategoryNameContaining(term);
        return modelMapper.map(categories, new TypeToken<List<CategoryDTO>>() {}.getType());
    }
}

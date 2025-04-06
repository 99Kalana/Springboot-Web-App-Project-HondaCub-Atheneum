package org.example.hondasupercub.controller;


import org.example.hondasupercub.dto.CategoryDTO;
import org.example.hondasupercub.dto.ResponseDTO;
import org.example.hondasupercub.service.impl.AdminCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/admincategories")
public class AdminCategoryController {
    @Autowired
    private AdminCategoryServiceImpl adminCategoryService;

    // ✅ Add a new category
    @PostMapping("save")
    public ResponseEntity<ResponseDTO> saveCategory(@RequestBody CategoryDTO categoryDTO) {
        adminCategoryService.addCategory(categoryDTO);
        ResponseDTO responseDTO = new ResponseDTO(201, "Category Saved", categoryDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // ✅ Get all categories
    @GetMapping("getAll")
    public ResponseEntity<ResponseDTO> getAllCategories() {
        List<CategoryDTO> categories = adminCategoryService.getAllCategories();
        ResponseDTO responseDTO = new ResponseDTO(200, "Category List", categories);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // ✅ Update category
    @PutMapping("update")
    public ResponseEntity<ResponseDTO> updateCategory(@RequestBody CategoryDTO categoryDTO) {
        adminCategoryService.updateCategory(categoryDTO);
        ResponseDTO responseDTO = new ResponseDTO(200, "Category Updated", categoryDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // ✅ Delete category
    @DeleteMapping("delete/{id}")
    public ResponseEntity<ResponseDTO> deleteCategory(@PathVariable("id") int id) {
        adminCategoryService.deleteCategory(id);
        ResponseDTO responseDTO = new ResponseDTO(200, "Category Deleted", null);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // ✅ Get category by ID
    @GetMapping("get/{id}")
    public ResponseEntity<ResponseDTO> getCategoryById(@PathVariable("id") int id) {
        CategoryDTO category = adminCategoryService.getCategoryById(id);
        if (category != null) {
            ResponseDTO responseDTO = new ResponseDTO(200, "Category Found", category);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            ResponseDTO responseDTO = new ResponseDTO(404, "Category Not Found", null);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
    }

    // ✅ Search categories
    @GetMapping("search")
    public ResponseEntity<ResponseDTO> searchCategories(@RequestParam String term) {
        List<CategoryDTO> categories = adminCategoryService.searchCategories(term);
        ResponseDTO responseDTO = new ResponseDTO(200, "Category Search Results", categories);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}

package com.sorokaandriy.restaurant_service.service;

import com.sorokaandriy.restaurant_service.dto.CategoryRequest;
import com.sorokaandriy.restaurant_service.dto.CategoryResponse;
import com.sorokaandriy.restaurant_service.entity.Category;
import com.sorokaandriy.restaurant_service.exception.CategoryNotFoundException;
import com.sorokaandriy.restaurant_service.repository.CategoryRepository;
import com.sorokaandriy.restaurant_service.service.mapper.RestaurantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriesService {

    private final CategoryRepository categoryRepository;
    private final RestaurantMapper restaurantMapper;

    public List<CategoryResponse> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> restaurantMapper.fromCategoryToCategoryResponse(category)).toList();
    }

    public CategoryResponse createCategory(CategoryRequest request){

        Category category = categoryRepository.save(Category.builder().name(request.name()).build());

        return restaurantMapper.fromCategoryToCategoryResponse(category);
    }

    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id " + id + " not found"));

        category.setName(request.name());
        categoryRepository.save(category);

        return restaurantMapper.fromCategoryToCategoryResponse(category);

    }
}

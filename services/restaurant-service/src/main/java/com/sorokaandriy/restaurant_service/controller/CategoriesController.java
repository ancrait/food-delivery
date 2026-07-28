package com.sorokaandriy.restaurant_service.controller;

import com.sorokaandriy.restaurant_service.dto.CategoryRequest;
import com.sorokaandriy.restaurant_service.dto.CategoryResponse;
import com.sorokaandriy.restaurant_service.entity.Category;
import com.sorokaandriy.restaurant_service.service.CategoriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoriesController {

    private final CategoriesService service;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAllCategories(){
        return ResponseEntity.ok(service.findAllCategories());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createCategory(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request
    ){
        return ResponseEntity.ok(service.updateCategory(id, request));
    }
}

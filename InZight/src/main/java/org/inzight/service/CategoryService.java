package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.CategoryRequest;
import org.inzight.dto.response.CategoryResponse;
import org.inzight.entity.Category;
import org.inzight.enums.CategoryType;
import org.inzight.mapper.CategoryMapper;
import org.inzight.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getCategories(String type) {
        if (type == null || type.isBlank()) {
            return categoryRepository.findAll()
                    .stream().map(categoryMapper::toResponse).toList();
        }

        CategoryType categoryType = CategoryType.valueOf(type.toUpperCase());
        return categoryRepository.findByType(categoryType)
                .stream().map(categoryMapper::toResponse).toList();
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}

package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.CategoryRequest;
import org.inzight.dto.response.CategoryResponse;
import org.inzight.entity.Category;
import org.inzight.enums.CategoryType;
import org.inzight.mapper.CategoryMapper;
import org.inzight.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    // ✅ base path cho icon (có thể đổi nếu deploy thật)
    @Value("${app.icon-base-url:/icons/}")
    private String iconBaseUrl;

    public List<CategoryResponse> getCategories(String type) {
        List<Category> categories;
        if (type == null || type.isBlank()) {
            categories = categoryRepository.findAll();
        } else {
            CategoryType categoryType = CategoryType.valueOf(type.toUpperCase());
            categories = categoryRepository.findByType(categoryType);
        }

        return categories.stream()
                .map(category -> {
                    CategoryResponse response = categoryMapper.toResponse(category);
                    // Gán icon URL tự động nếu chưa có trong DB
                    if (response.getIconUrl() == null || response.getIconUrl().isBlank()) {
                        response.setIconUrl(getDefaultIconForCategory(response.getName()));
                    }
                    return response;
                })
                .toList();
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        //  Gán icon mặc định khi tạo mới
        category.setIconUrl(getDefaultIconForCategory(request.getName()));
        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    //  Hàm ánh xạ tên category → icon tương ứng
    private String getDefaultIconForCategory(String name) {
        if (name == null) return iconBaseUrl + "default.png";
        String lower = name.toLowerCase();
        if (lower.contains("food")) return iconBaseUrl + "ic_food.png";
        if (lower.contains("grocer")) return iconBaseUrl + "ic_groceries.png";
        if (lower.contains("shop")) return iconBaseUrl + "ic_shopping.png";
        if (lower.contains("enter")) return iconBaseUrl + "ic_entertainment.png";
        if (lower.contains("transfer")) return iconBaseUrl + "ic_transfer.png";
        if (lower.contains("salary")) return iconBaseUrl + "ic_salary.png";
        return iconBaseUrl + "ic_default.png";
    }
}

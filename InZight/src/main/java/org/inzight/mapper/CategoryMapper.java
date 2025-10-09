package org.inzight.mapper;

import org.inzight.dto.request.CategoryRequest;
import org.inzight.dto.response.CategoryResponse;
import org.inzight.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category toEntity(CategoryRequest request);

    @Mapping(source = "id", target = "id")
    CategoryResponse toResponse(Category category);
}

package org.inzight.mapper;

import org.inzight.dto.request.BudgetRequest;
import org.inzight.dto.response.BudgetResponse;
import org.inzight.entity.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    Budget toEntity(BudgetRequest request);

    BudgetResponse toResponse(Budget budget);

    void updateEntity(@MappingTarget Budget budget, BudgetRequest request);
}

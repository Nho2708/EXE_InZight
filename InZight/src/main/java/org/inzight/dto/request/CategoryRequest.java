package org.inzight.dto.request;

import lombok.Data;
import org.inzight.enums.CategoryType;

@Data
public class CategoryRequest {
    private String name;
    private CategoryType type; // INCOME or EXPENSE
}

package org.inzight.dto.response;

import lombok.*;
import org.inzight.enums.CategoryType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private CategoryType type;
    private String iconUrl; //  thêm icon
}

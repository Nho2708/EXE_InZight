package org.inzight.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Builder
public class PostRequest {


    private String content;
    private String imageUrl;
}

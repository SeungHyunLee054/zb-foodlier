package com.zerobase.foodlier.module.recipe.dto.recipe;

import com.zerobase.foodlier.module.recipe.domain.vo.RecipeDetail;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageUrlDto {
    private String mainImageUrl;
    private List<String> cookingOrderImageUrlList;

}

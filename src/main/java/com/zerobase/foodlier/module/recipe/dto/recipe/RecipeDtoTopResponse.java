package com.zerobase.foodlier.module.recipe.dto.recipe;

import com.zerobase.foodlier.module.recipe.domain.model.Recipe;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDtoTopResponse {

    private Long recipeId;
    private String mainImageUrl;
    private String title;
    private String content;
    private int heartCount;

    public static RecipeDtoTopResponse from(Recipe recipe){
        return RecipeDtoTopResponse
                .builder()
                .recipeId(recipe.getId())
                .mainImageUrl(recipe.getMainImageUrl())
                .title(recipe.getSummary().getTitle())
                .content(recipe.getSummary().getContent())
                .heartCount(recipe.getHeartCount())
                .build();
    }
}

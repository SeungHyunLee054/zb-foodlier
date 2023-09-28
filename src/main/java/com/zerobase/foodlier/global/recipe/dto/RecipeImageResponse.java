package com.zerobase.foodlier.global.recipe.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeImageResponse {

    private String mainImage;
    private List<String> cookingOrderImageList;

}

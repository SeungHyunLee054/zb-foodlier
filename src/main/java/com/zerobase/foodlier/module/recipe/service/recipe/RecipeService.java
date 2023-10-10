package com.zerobase.foodlier.module.recipe.service.recipe;

import com.zerobase.foodlier.common.response.ListResponse;
import com.zerobase.foodlier.module.member.member.domain.model.Member;
import com.zerobase.foodlier.module.recipe.domain.model.Recipe;
import com.zerobase.foodlier.module.recipe.dto.recipe.ImageUrlDto;
import com.zerobase.foodlier.module.recipe.dto.recipe.RecipeDtoRequest;
import com.zerobase.foodlier.module.recipe.dto.recipe.RecipeDtoResponse;
import org.springframework.data.domain.Pageable;

public interface RecipeService {
    void createRecipe(Member member, RecipeDtoRequest recipeDtoRequest);

    void updateRecipe(RecipeDtoRequest recipeDtoRequest, Long id);

    Recipe getRecipe(Long id);

    RecipeDtoResponse getRecipeDetail(Long id);

    void deleteRecipe(Long id);

    ListResponse<Recipe> getRecipeByTitle(String recipeTitle, Pageable pageable);

    ImageUrlDto getBeforeImageUrl(Long id);

    void plusReviewStar(Long recipeId, int star);
    void updateReviewStar(Long recipeId, int originStar, int newStar);
    void minusReviewStar(Long recipeId, int star);
    Recipe plusCommentCount(Long recipeId);
    void minusCommentCount(Long recipeId);
}
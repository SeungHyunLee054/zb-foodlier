package com.zerobase.foodlier.module.recipe.service;

import com.zerobase.foodlier.module.member.member.domain.model.Member;
import com.zerobase.foodlier.module.recipe.domain.document.RecipeDocument;
import com.zerobase.foodlier.module.recipe.domain.model.Recipe;
import com.zerobase.foodlier.module.recipe.domain.vo.RecipeIngredient;
import com.zerobase.foodlier.module.recipe.domain.vo.RecipeStatistics;
import com.zerobase.foodlier.module.recipe.domain.vo.Summary;
import com.zerobase.foodlier.module.recipe.dto.*;
import com.zerobase.foodlier.module.recipe.exception.RecipeException;
import com.zerobase.foodlier.module.recipe.repository.RecipeRepository;
import com.zerobase.foodlier.module.recipe.repository.RecipeSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.zerobase.foodlier.module.recipe.exception.RecipeErrorCode.NO_SUCH_RECIPE;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeSearchRepository recipeSearchRepository;

    /**
     * 작성자: 황태원(이종욱)
     * 레시피 정보를 받아 레시피를 등록
     * 레시피 등록 시 레시피 검색을 위한 객체도 레시피 정보를 기반으로 저장
     * 작성일자: 2023-09-27
     */
    @Override
    public void createRecipe(Member member, RecipeDtoRequest recipeDtoRequest) {
        Recipe recipe = Recipe.builder()
                .summary(Summary.builder()
                        .title(recipeDtoRequest.getTitle())
                        .content(recipeDtoRequest.getContent())
                        .build())
                .mainImageUrl(recipeDtoRequest.getMainImageUrl())
                .expectedTime(recipeDtoRequest.getExpectedTime())
                .recipeStatistics(new RecipeStatistics())
                .difficulty(recipeDtoRequest.getDifficulty())
                .isPublic(true)
                .member(member)
                .recipeIngredientList(recipeDtoRequest.getRecipeIngredientDtoList()
                        .stream()
                        .map(RecipeIngredientDto::toEntity)
                        .collect(Collectors.toList()))
                .recipeDetailList(recipeDtoRequest.getRecipeDetailDtoList()
                        .stream()
                        .map(RecipeDetailDto::toEntity)
                        .collect(Collectors.toList()))
                .build();
        recipeRepository.save(recipe);
        recipeSearchRepository.save(RecipeDocument.builder()
                .id(recipe.getId())
                .title(recipe.getSummary().getTitle())
                .chefName(recipe.getMember().getNickname())
                .ingredients(recipe.getRecipeIngredientList().stream()
                        .map(RecipeIngredient::getName)
                        .collect(Collectors.toList()))
                .numberOfHeart(recipe.getHeartList().size())
                .numberOfComment(recipe.getCommentList().size())
                .build());
    }

    /**
     * 작성자: 황태원(이종욱)
     * 레시피 수정 정보를 받아 레시피를 수정
     * 레시피 수정 시 레시피 검색을 위한 객체도 레시피 정보를 기반으로 수정
     * 작성일자: 2023-09-27
     */
    @Transactional
    @Override
    public void updateRecipe(RecipeDtoRequest recipeDtoRequest, Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeException(NO_SUCH_RECIPE));

        recipe.setSummary(Summary.builder()
                .title(recipeDtoRequest.getTitle())
                .content(recipeDtoRequest.getContent())
                .build());
        recipe.setMainImageUrl(recipeDtoRequest.getMainImageUrl());
        recipe.setExpectedTime(recipeDtoRequest.getExpectedTime());
        recipe.setDifficulty(recipeDtoRequest.getDifficulty());
        recipe.setRecipeDetailList(recipeDtoRequest.getRecipeDetailDtoList()
                .stream()
                .map(RecipeDetailDto::toEntity)
                .collect(Collectors.toList()));
        recipe.setRecipeIngredientList(recipeDtoRequest.getRecipeIngredientDtoList()
                .stream()
                .map(RecipeIngredientDto::toEntity)
                .collect(Collectors.toList()));

        recipeRepository.save(recipe);
        recipeSearchRepository.save(RecipeDocument.builder()
                .id(recipe.getId())
                .title(recipe.getSummary().getTitle())
                .chefName(recipe.getMember().getNickname())
                .ingredients(recipe.getRecipeIngredientList().stream()
                        .map(RecipeIngredient::getName)
                        .collect(Collectors.toList()))
                .numberOfHeart(recipe.getHeartList().size())
                .numberOfComment(recipe.getCommentList().size())
                .build());
    }

    @Override
    public Recipe getRecipe(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeException(NO_SUCH_RECIPE));
    }

    /**
     * 2023-09-26
     * 황태원
     * 레시피 상세보기, 레시피 수정 시 정보 로드
     */
    @Override
    public RecipeDtoResponse getRecipeDetail(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeException(NO_SUCH_RECIPE));

        return RecipeDtoResponse.fromEntity(recipe);
    }

    /**
     * 작성자: 황태원(이종욱)
     * 레시피 id를 이용하여 등록된 레시피 삭제
     * 레시피 삭제로 인해 해당 레시피에 대한 검색 객체 삭제
     * 작성일자: 2023-09-27
     */
    @Override
    public ImageUrlDto deleteRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeException(NO_SUCH_RECIPE));
        recipeRepository.deleteById(id);
        recipeSearchRepository.deleteById(recipe.getId());
        return ImageUrlDto.builder()
                .mainImageUrl(recipe.getMainImageUrl())
                .recipeDetailList(recipe.getRecipeDetailList())
                .build();
    }

    /**

     - 작성자: 이종욱
     - 레시피 제목을 이용하여 유사한 제목을 갖는 레시피 목록 반환
     - 작성일자: 2023-09-27
     */

    @Override
    public List<Recipe> getRecipeByTitle(String recipeTitle, Pageable pageable) {
        Page<RecipeDocument> byTitle = recipeSearchRepository.findByTitle(recipeTitle, pageable);
        List<Recipe> recipeList = new ArrayList<>();
        for (RecipeDocument recipeDocument : byTitle.toList()) {
            recipeList.add(recipeRepository.findById(recipeDocument.getId())
                    .orElseThrow(() -> new RecipeException(NO_SUCH_RECIPE)));
        }

        return recipeList;

    }

}

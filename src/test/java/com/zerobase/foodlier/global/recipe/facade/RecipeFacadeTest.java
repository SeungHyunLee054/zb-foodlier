package com.zerobase.foodlier.global.recipe.facade;

import com.zerobase.foodlier.common.s3.service.S3Service;
import com.zerobase.foodlier.global.recipe.dto.RecipeImageResponse;
import com.zerobase.foodlier.module.member.member.domain.model.Member;
import com.zerobase.foodlier.module.member.member.exception.MemberException;
import com.zerobase.foodlier.module.member.member.service.MemberService;
import com.zerobase.foodlier.module.recipe.domain.model.Recipe;
import com.zerobase.foodlier.module.recipe.domain.vo.RecipeDetail;
import com.zerobase.foodlier.module.recipe.dto.ImageUrlDto;
import com.zerobase.foodlier.module.recipe.dto.RecipeDtoRequest;
import com.zerobase.foodlier.module.recipe.exception.RecipeException;
import com.zerobase.foodlier.module.recipe.service.RecipeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.zerobase.foodlier.module.member.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.zerobase.foodlier.module.recipe.domain.type.Difficulty.HARD;
import static com.zerobase.foodlier.module.recipe.exception.RecipeErrorCode.NO_PERMISSION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecipeFacadeTest {
    @Mock
    private S3Service s3Service;

    @Mock
    private MemberService memberService;

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeFacade recipeFacade;

    @Test
    @DisplayName("이미지 s3 등록 후 이미지 url return 성공")
    void success_upload_recipe_image() {
        //given
        String imageName1 = "img1.jpg";
        String content1 = "content1";
        MultipartFile mainImage = new MockMultipartFile(imageName1,
                imageName1, "image/jpg", content1.getBytes());
        String expectedMainImageUrl = "https://zb-foodlier.s3.ap-northeast-2.amazonaws.com/img1.jpg";

        String imageName2 = "img2.jpg";
        String content2 = "content2";
        MultipartFile cookingOrderImage1 = new MockMultipartFile(imageName2,
                imageName2, "image/jpg", content2.getBytes());
        String expectedCookingOrderImageUrl1 = "https://zb-foodlier.s3.ap-northeast-2.amazonaws.com/img2.jpg";

        String imageName3 = "img3.jpg";
        String content3 = "content3";
        MultipartFile cookingOrderImage2 = new MockMultipartFile(imageName3,
                imageName3, "image/jpg", content3.getBytes());
        String expectedCookingOrderImageUrl2 = "https://zb-foodlier.s3.ap-northeast-2.amazonaws.com/img3.jpg";

        List<MultipartFile> cookingOrderImageList =
                new ArrayList<>(List.of(cookingOrderImage1, cookingOrderImage2));
        List<String> expectedCookingOrderImageUrlList =
                new ArrayList<>(List.of(expectedCookingOrderImageUrl1, expectedCookingOrderImageUrl2));

        given(s3Service.getImageUrl(mainImage)).willReturn(expectedMainImageUrl);
        given(s3Service.getImageUrlList(cookingOrderImageList))
                .willReturn(expectedCookingOrderImageUrlList);

        //when
        RecipeImageResponse recipeImageResponse = recipeFacade
                .uploadRecipeImage(mainImage, cookingOrderImageList);

        //then
        assertAll(
                () -> assertEquals(expectedMainImageUrl, recipeImageResponse.getMainImage()),
                () -> assertEquals(expectedCookingOrderImageUrlList, recipeImageResponse.getCookingOrderImageList())
        );
    }

    @Test
    @DisplayName("꿀조합 게시글 수정 시 이미지 등록 후 url return 성공")
    void success_update_recipe_image() {
        //given
        String email = "email@email.com";
        Long id = 1L;
        String imageName1 = "img1.jpg";
        String content1 = "content1";
        MultipartFile mainImage = new MockMultipartFile(imageName1,
                imageName1, "image/jpg", content1.getBytes());
        String expectedMainImageUrl = "https://zb-foodlier.s3.ap-northeast-2.amazonaws.com/img1.jpg";

        String imageName2 = "img2.jpg";
        String content2 = "content2";
        MultipartFile cookingOrderImage1 = new MockMultipartFile(imageName2,
                imageName2, "image/jpg", content2.getBytes());
        String expectedCookingOrderImageUrl1 = "https://zb-foodlier.s3.ap-northeast-2.amazonaws.com/img2.jpg";

        String imageName3 = "img3.jpg";
        String content3 = "content3";
        MultipartFile cookingOrderImage2 = new MockMultipartFile(imageName3,
                imageName3, "image/jpg", content3.getBytes());
        String expectedCookingOrderImageUrl2 = "https://zb-foodlier.s3.ap-northeast-2.amazonaws.com/img3.jpg";

        List<MultipartFile> cookingOrderImageList =
                new ArrayList<>(List.of(cookingOrderImage1, cookingOrderImage2));
        List<String> expectedCookingOrderImageUrlList =
                new ArrayList<>(List.of(expectedCookingOrderImageUrl1, expectedCookingOrderImageUrl2));

        given(s3Service.getImageUrl(mainImage)).willReturn(expectedMainImageUrl);
        given(s3Service.getImageUrlList(cookingOrderImageList))
                .willReturn(expectedCookingOrderImageUrlList);
        given(memberService.findByEmail(email))
                .willReturn(Member.builder()
                        .id(id)
                        .build());
        given(recipeService.getRecipe(id))
                .willReturn(Recipe.builder()
                        .member(Member.builder()
                                .id(id)
                                .build())
                        .build());

        //when
        RecipeImageResponse recipeImageResponse = recipeFacade
                .updateRecipeImage(email, mainImage, cookingOrderImageList, id);

        //then
        assertAll(
                () -> assertEquals(expectedMainImageUrl, recipeImageResponse.getMainImage()),
                () -> assertEquals(expectedCookingOrderImageUrlList, recipeImageResponse.getCookingOrderImageList())
        );
    }

    @Test
    @DisplayName("꿀조합 게시글 수정 시 이미지 등록 후 url return 실패 - 권한없음")
    void fail_update_recipe_image_no_permission() {
        //given
        String email = "email@email.com";
        Long id = 1L;
        Long wrongId = 2L;
        Long recipeId = 3L;

        String imageName1 = "img1.jpg";
        String content1 = "content1";
        MultipartFile mainImage = new MockMultipartFile(imageName1,
                imageName1, "image/jpg", content1.getBytes());

        String imageName2 = "img2.jpg";
        String content2 = "content2";
        MultipartFile cookingOrderImage1 = new MockMultipartFile(imageName2,
                imageName2, "image/jpg", content2.getBytes());

        String imageName3 = "img3.jpg";
        String content3 = "content3";
        MultipartFile cookingOrderImage2 = new MockMultipartFile(imageName3,
                imageName3, "image/jpg", content3.getBytes());

        List<MultipartFile> cookingOrderImageList =
                new ArrayList<>(List.of(cookingOrderImage1, cookingOrderImage2));

        given(memberService.findByEmail(email)).willReturn(Member.builder()
                .id(id)
                .build());
        given(recipeService.getRecipe(recipeId)).willReturn(Recipe.builder()
                .member(Member.builder()
                        .id(wrongId)
                        .build())
                .build());

        //when
        RecipeException recipeException = assertThrows(RecipeException.class,
                () -> recipeFacade.updateRecipeImage(email, mainImage, cookingOrderImageList, recipeId));

        //then
        assertEquals(NO_PERMISSION, recipeException.getErrorCode());
    }

    @Test
    @DisplayName("꿀조합 게시글 작성 성공")
    void success_create_recipe() {
        //given
        String email = "email@email.com";
        RecipeDtoRequest recipeDtoRequest = RecipeDtoRequest.builder()
                .title("title")
                .content("content")
                .mainImageUrl("https://s3/image.jpg")
                .difficulty(HARD)
                .expectedTime(30)
                .build();

        ArgumentCaptor<RecipeDtoRequest> captor =
                ArgumentCaptor.forClass(RecipeDtoRequest.class);

        //when
        recipeFacade.createRecipe(email, recipeDtoRequest);

        //then
        verify(recipeService, times(1)).createRecipe(any(), captor.capture());
        RecipeDtoRequest request = captor.getValue();
        assertAll(
                () -> assertEquals("title", request.getTitle()),
                () -> assertEquals("content", request.getContent()),
                () -> assertEquals("https://s3/image.jpg", request.getMainImageUrl()),
                () -> assertEquals(HARD, request.getDifficulty()),
                () -> assertEquals(30, request.getExpectedTime())
        );
    }

    @Test
    @DisplayName("꿀조합 게시글 작성 실패 - 사용자 없음")
    void fail_create_recipe_member_not_found() {
        //given
        String email = "email@email.com";
        RecipeDtoRequest recipeDtoRequest = RecipeDtoRequest.builder()
                .title("title")
                .content("content")
                .mainImageUrl("https://s3/image.jpg")
                .difficulty(HARD)
                .expectedTime(30)
                .build();

        given(memberService.findByEmail(email))
                .willThrow(new MemberException(MEMBER_NOT_FOUND));

        //when
        MemberException memberException = assertThrows(MemberException.class,
                () -> recipeFacade.createRecipe(email, recipeDtoRequest));

        //then
        assertEquals(MEMBER_NOT_FOUND, memberException.getErrorCode());
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void success_update_recipe() {
        //given
        String email = "email@email.com";
        Long recipeId = 10L;
        Long id = 1L;
        RecipeDtoRequest recipeDtoRequest = RecipeDtoRequest.builder()
                .title("title")
                .content("content")
                .mainImageUrl("https://s3/image.jpg")
                .difficulty(HARD)
                .expectedTime(30)
                .build();
        Recipe recipe = Recipe.builder()
                .id(recipeId)
                .mainImageUrl("https://zb-foodlier.s3.ap-northeast-2.amazonaws.com/img1.jpg")
                .recipeDetailList(new ArrayList<>(List.of(
                        RecipeDetail.builder()
                                .cookingOrderImageUrl("https://zb-foodlier.s3.ap-northeast-2.amazonaws.com/img2.jpg")
                                .build(),
                        RecipeDetail.builder()
                                .cookingOrderImageUrl("https://zb-foodlier.s3.ap-northeast-2.amazonaws.com/img3.jpg")
                                .build()
                )))
                .member(Member.builder()
                        .id(id)
                        .build())
                .build();

        given(memberService.findByEmail(email)).willReturn(Member.builder()
                .id(id)
                .build());
        given(recipeService.getRecipe(recipeId)).willReturn(recipe);

        // when
        recipeFacade.updateRecipe(email, recipeDtoRequest, recipeId);

        // then
        verify(recipeService, times(1)).updateRecipe(recipeDtoRequest, recipeId);
        verify(s3Service, times(3)).deleteImage(any());
    }

    @Test
    @DisplayName("게시글 수정 실패 - 권한 없음")
    void fail_update_recipe_no_permission() {
        //given
        String email = "email@email.com";
        Long recipeId = 10L;
        Long id = 9L;
        Long wrongId = 8L;
        RecipeDtoRequest recipeDtoRequest = RecipeDtoRequest.builder().build();

        given(memberService.findByEmail(email)).willReturn(Member.builder()
                .id(id)
                .build());
        given(recipeService.getRecipe(recipeId)).willReturn(Recipe.builder()
                .member(Member.builder()
                        .id(wrongId)
                        .build())
                .build());

        //when
        RecipeException recipeException = assertThrows(RecipeException.class,
                () -> recipeFacade.updateRecipe(email, recipeDtoRequest, recipeId));

        //then
        assertEquals(NO_PERMISSION, recipeException.getErrorCode());
    }

    @Test
    @DisplayName("꿀조합 게시글 삭제, 삭제 후 이미지 삭제 성공")
    void success_delete_recipe() {
        //given
        String email = "email@email.com";
        Long recipeId = 10L;
        Long id = 1L;
        String mainImageUrl = "https://zb-foodlier.s3.ap-northeast-2.amazonaws.com/img1.jpg";
        String cookingOrderImageUrl1 = "https://zb-foodlier.s3.ap-northeast-2.amazonaws.com/img2.jpg";
        String cookingOrderImageUrl2 = "https://zb-foodlier.s3.ap-northeast-2.amazonaws.com/img3.jpg";

        given(memberService.findByEmail(email)).willReturn(Member.builder()
                .id(id)
                .build());
        given(recipeService.getRecipe(recipeId)).willReturn(Recipe.builder()
                .member(Member.builder()
                        .id(id)
                        .build())
                .build());
        given(recipeService.deleteRecipe(recipeId))
                .willReturn(ImageUrlDto.builder()
                        .mainImageUrl(mainImageUrl)
                        .recipeDetailList(new ArrayList<>(List.of(
                                RecipeDetail.builder()
                                        .cookingOrderImageUrl(cookingOrderImageUrl1)
                                        .build(),
                                RecipeDetail.builder()
                                        .cookingOrderImageUrl(cookingOrderImageUrl2)
                                        .build())))
                        .build());

        //when
        recipeFacade.deleteRecipe(email, recipeId);

        //then
        verify(s3Service, times(3)).deleteImage(any());
    }

    @Test
    @DisplayName("꿀조합 게시글 삭제, 삭제 후 이미지 삭제 실패 - 권한없음")
    void fail_delete_recipe_no_permission() {
        //given
        String email = "email@email.com";
        Long recipeId = 10L;
        Long id = 9L;
        Long wrongId = 8L;

        given(memberService.findByEmail(email)).willReturn(Member.builder()
                .id(id)
                .build());
        given(recipeService.getRecipe(recipeId)).willReturn(Recipe.builder()
                .member(Member.builder()
                        .id(wrongId)
                        .build())
                .build());

        //when
        RecipeException recipeException = assertThrows(RecipeException.class,
                () -> recipeFacade.deleteRecipe(email, recipeId));

        //then
        assertEquals(NO_PERMISSION, recipeException.getErrorCode());
    }

    @Test
    @DisplayName("게시글 수정, 삭제권한 검증 성공")
    void success_check_permission() {
        //given
        String email = "email@email.com";
        Long recipeId = 10L;
        Long id = 9L;

        given(memberService.findByEmail(email)).willReturn(Member.builder()
                .id(id)
                .build());
        given(recipeService.getRecipe(recipeId)).willReturn(Recipe.builder()
                .member(Member.builder()
                        .id(id)
                        .build())
                .build());

        //when

        //then
        assertDoesNotThrow(() -> recipeFacade.checkPermission(email, recipeId));
    }

    @Test
    @DisplayName("게시글 수정, 삭제권한 검증 실패 - 권한없음")
    void fail_check_permission_no_permission() {
        //given
        String email = "email@email.com";
        Long recipeId = 10L;
        Long id = 9L;
        Long wrongId = 8L;

        given(memberService.findByEmail(email)).willReturn(Member.builder()
                .id(id)
                .build());
        given(recipeService.getRecipe(recipeId)).willReturn(Recipe.builder()
                .member(Member.builder()
                        .id(wrongId)
                        .build())
                .build());

        //when
        RecipeException recipeException = assertThrows(RecipeException.class,
                () -> recipeFacade.checkPermission(email, recipeId));

        //then
        assertEquals(NO_PERMISSION, recipeException.getErrorCode());
    }
}
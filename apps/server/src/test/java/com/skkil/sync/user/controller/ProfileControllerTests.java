package com.skkil.sync.user.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.Schema.schema;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestSecurityConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import com.skkil.sync.config.SecurityConfig;
import com.skkil.sync.user.constant.Role;
import com.skkil.sync.user.dto.request.UpdateProfileRequest;
import com.skkil.sync.user.dto.response.GetProfileResponse;
import com.skkil.sync.user.exception.EmailNotVerifiedException;
import com.skkil.sync.user.exception.HandleNotSetException;
import com.skkil.sync.user.service.ProfileService;
import java.util.function.Function;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Import({SecurityConfig.class, TestSecurityConfig.class})
class ProfileControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  @MockitoBean private ProfileService profileService;

  @Test
  @WithAuthenticatedUser
  void getAuthenticatedUser() throws Exception {
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.getAuthenticatedUser();
    GetProfileResponse response = createGetProfileResponse(user.userId());

    when(profileService.getProfileById(eq(user), eq(user.userId()))).thenReturn(response);

    mockMvc
        .perform(get("/profiles/me"))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetAuthenticatedUser",
                ResourceSnippetParameters.builder()
                    .tag("profile")
                    .summary("Get Authenticated User Profile")
                    .description("Get Authenticated User Profile")
                    .responseSchema(schema(GetProfileResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                getProfileResponseFields()));
  }

  @Test
  void getProfileByHandle() throws Exception {
    GetProfileResponse response = createGetProfileResponse(1L);

    when(profileService.getProfileByHandle(eq(null), eq(response.handle()))).thenReturn(response);

    mockMvc
        .perform(get("/profiles/{handle}", response.handle()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "GetProfileByHandle",
                ResourceSnippetParameters.builder()
                    .tag("profile")
                    .summary("Get Profile By Handle")
                    .description("Get Profile By Handle")
                    .responseSchema(schema(GetProfileResponse.class.getSimpleName())),
                null,
                null,
                Function.identity(),
                pathParameters(parameterWithName("handle").description("Handle")),
                getProfileResponseFields()));
  }

  @Test
  @WithAuthenticatedUser
  void updateProfile() throws Exception {
    UpdateProfileRequest request = createUpdateProfileRequest();

    mockMvc
        .perform(
            patch("/profiles/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsBytes(request))
                .with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "UpdateProfile",
                ResourceSnippetParameters.builder()
                    .tag("profile")
                    .summary("Update Profile")
                    .description("Update Profile")
                    .requestSchema(schema(UpdateProfileRequest.class.getSimpleName())),
                null,
                preprocessResponse(prettyPrint()),
                Function.identity(),
                updateProfileRequestFields()));
  }

  @Test
  @DisplayName("[onboard] API 문서화 테스트")
  @WithAuthenticatedUser(id = 1L)
  void onboard() throws Exception {
    mockMvc
        .perform(post("/profiles/me/onboard").contentType(MediaType.APPLICATION_JSON).with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "OnboardProfile",
                ResourceSnippetParameters.builder()
                    .tag("profile")
                    .summary("Complete Onboarding")
                    .description("핸들 설정 및 이메일 인증 여부를 검증한 뒤 온보딩을 완료 처리합니다."),
                null,
                null,
                Function.identity()));

    verify(profileService).completeOnboarding(eq(1L));
  }

  @Test
  @DisplayName("[onboard] 로그인하지 않은 사용자는 접근할 수 없다")
  void onboard_unauthenticatedUser_shouldReturnUnauthorized() throws Exception {
    mockMvc.perform(post("/profiles/me/onboard").with(csrf())).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("[onboard] 핸들이 설정되어 있지 않으면 400을 반환한다")
  @WithAuthenticatedUser(id = 1L)
  void onboard_handleNotSet_returnsBadRequest() throws Exception {
    doThrow(new HandleNotSetException()).when(profileService).completeOnboarding(eq(1L));

    mockMvc.perform(post("/profiles/me/onboard").with(csrf())).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("[onboard] 이메일이 인증되어 있지 않으면 409를 반환한다")
  @WithAuthenticatedUser(id = 1L)
  void onboard_emailNotVerified_returnsConflict() throws Exception {
    doThrow(new EmailNotVerifiedException()).when(profileService).completeOnboarding(eq(1L));

    mockMvc.perform(post("/profiles/me/onboard").with(csrf())).andExpect(status().isConflict());
  }

  private static GetProfileResponse createGetProfileResponse(Long userId) {
    GetProfileResponse response =
        GetProfileResponse.builder()
            .userId(userId.toString())
            .handle("username")
            .name("name")
            .email("email@example.com")
            .bio("bio")
            .profession("profession")
            .profileImageUrl("https://example.com/profile-image.png")
            .role(Role.USER)
            .isFollowing(true)
            .followerCount(3L)
            .followingCount(5L)
            .isOnboarded(true)
            .isEmailVerified(true)
            .isAuthenticatedUser(false)
            .build();

    return response;
  }

  private static UpdateProfileRequest createUpdateProfileRequest() {
    UpdateProfileRequest request =
        UpdateProfileRequest.builder()
            .name("name")
            .handle("username")
            .profileImageId("1")
            .removeProfileImage(false)
            .bio("bio")
            .profession("profession")
            .contacts(
                new UpdateProfileRequest.Contacts(
                    "https://example.com",
                    "linkedinUser",
                    "githubUser",
                    "instagramUser",
                    "twitterUser"))
            .build();

    return request;
  }

  private static ResponseFieldsSnippet getProfileResponseFields() {
    return responseFields(
        fieldWithPath("userId").type(JsonFieldType.STRING).description("ID"),
        fieldWithPath("handle").type(JsonFieldType.STRING).description("Handle").optional(),
        fieldWithPath("name").type(JsonFieldType.STRING).description("Full Name"),
        fieldWithPath("email").type(JsonFieldType.STRING).description("E-mail"),
        fieldWithPath("bio").type(JsonFieldType.STRING).description("Bio"),
        fieldWithPath("profession").type(JsonFieldType.STRING).description("Profession"),
        fieldWithPath("profileImageUrl")
            .type(JsonFieldType.STRING)
            .description("Profile Image URL"),
        fieldWithPath("isFollowing").type(JsonFieldType.BOOLEAN).description("Is Following"),
        fieldWithPath("followerCount").type(JsonFieldType.NUMBER).description("Follower Count"),
        fieldWithPath("followingCount").type(JsonFieldType.NUMBER).description("Following Count"),
        fieldWithPath("isOnboarded")
            .type(JsonFieldType.BOOLEAN)
            .description("Is Onboarded (본인 프로필 조회 시에만 포함, 그 외에는 null)")
            .optional(),
        fieldWithPath("isEmailVerified")
            .type(JsonFieldType.BOOLEAN)
            .description("Is Email Verified (본인 프로필 조회 시에만 포함, 그 외에는 null)")
            .optional(),
        fieldWithPath("isAuthenticatedUser")
            .type(JsonFieldType.BOOLEAN)
            .description("Is Authenticated User"),
        fieldWithPath("role").type(JsonFieldType.STRING).description("Role"),
        fieldWithPath("contacts").optional().type(JsonFieldType.OBJECT).description("Contacts"),
        fieldWithPath("contacts.custom")
            .optional()
            .type(JsonFieldType.STRING)
            .description("Custom Contact"),
        fieldWithPath("contacts.linkedin")
            .optional()
            .type(JsonFieldType.STRING)
            .description("LinkedIn Username"),
        fieldWithPath("contacts.github")
            .optional()
            .type(JsonFieldType.STRING)
            .description("GitHub Username"),
        fieldWithPath("contacts.instagram")
            .optional()
            .type(JsonFieldType.STRING)
            .description("Instagram Username"),
        fieldWithPath("contacts.twitter")
            .optional()
            .type(JsonFieldType.STRING)
            .description("Twitter Username"));
  }

  private static RequestFieldsSnippet updateProfileRequestFields() {
    return requestFields(
        fieldWithPath("name").type(JsonFieldType.STRING).description("Full Name").optional(),
        fieldWithPath("handle").type(JsonFieldType.STRING).description("Handle").optional(),
        fieldWithPath("profileImageId")
            .type(JsonFieldType.STRING)
            .description("Profile Image ID")
            .optional(),
        fieldWithPath("removeProfileImage")
            .type(JsonFieldType.BOOLEAN)
            .description("Remove Profile Image")
            .optional(),
        fieldWithPath("bio").type(JsonFieldType.STRING).description("Bio").optional(),
        fieldWithPath("profession").type(JsonFieldType.STRING).description("Profession").optional(),
        fieldWithPath("contacts").type(JsonFieldType.OBJECT).description("Contacts").optional(),
        fieldWithPath("contacts.custom")
            .type(JsonFieldType.STRING)
            .description("Custom Contact")
            .optional(),
        fieldWithPath("contacts.linkedin")
            .type(JsonFieldType.STRING)
            .description("LinkedIn Username")
            .optional(),
        fieldWithPath("contacts.github")
            .type(JsonFieldType.STRING)
            .description("GitHub Username")
            .optional(),
        fieldWithPath("contacts.instagram")
            .type(JsonFieldType.STRING)
            .description("Instagram Username")
            .optional(),
        fieldWithPath("contacts.twitter")
            .type(JsonFieldType.STRING)
            .description("Twitter Username")
            .optional());
  }
}

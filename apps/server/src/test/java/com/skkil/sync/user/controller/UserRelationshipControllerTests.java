package com.skkil.sync.user.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.user.service.UserRelationshipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserRelationshipController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class UserRelationshipControllerTests {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserRelationshipService userRelationshipService;

  @Test
  @WithAuthenticatedUser
  void followUser() throws Exception {
    doNothing().when(userRelationshipService).followUser(eq(1L), eq(2L));

    mockMvc
        .perform(post("/users/follow/{followeeId}", 2L).with(csrf()))
        .andExpect(status().isOk())
        .andDo(
            document(
                "users-follow",
                pathParameters(parameterWithName("followeeId").description("Followee user ID"))));
  }

  @Test
  @WithAuthenticatedUser
  void unfollowUser() throws Exception {
    doNothing().when(userRelationshipService).unfollowUser(eq(1L), eq(2L));

    mockMvc
        .perform(delete("/users/unfollow/{followeeId}", 2L).with(csrf()))
        .andExpect(status().isNoContent())
        .andDo(
            document(
                "users-unfollow",
                pathParameters(parameterWithName("followeeId").description("Followee user ID"))));
  }
}

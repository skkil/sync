package com.skkil.sync.provider.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import com.skkil.sync.provider.project.dto.response.GetTeamBuildingPostsResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetTeamBuildingPostsResponseSnippets {

  public static GetTeamBuildingPostsResponse getGetTeamBuildingPostsResponse() {
    GetTeamBuildingPostsResponse.Project project =
        new GetTeamBuildingPostsResponse.Project("1", "Project Name", "Project Description");

    GetTeamBuildingPostsResponse.Post post =
        new GetTeamBuildingPostsResponse.Post("1", project, "Post Title", "Post Content");

    return new GetTeamBuildingPostsResponse(CursorPaginationResponseSnippets.of(List.of(post)));
  }

  public static ResponseFieldsSnippet getTeamBuildingPostsResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("posts");

    fields =
        fields.andWithPrefix(
            "posts.nodes[].content",
            fieldWithPath(".id").type(JsonFieldType.STRING).description("Post ID"),
            fieldWithPath(".title").type(JsonFieldType.STRING).description("Post Title"),
            fieldWithPath(".content").type(JsonFieldType.STRING).description("Post Content"),
            fieldWithPath(".project").type(JsonFieldType.OBJECT).description("Project"));

    fields =
        fields.andWithPrefix(
            "posts.nodes[].content.project",
            fieldWithPath(".id").type(JsonFieldType.STRING).description("Project ID"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Project Name"),
            fieldWithPath(".description")
                .type(JsonFieldType.STRING)
                .description("Project Description"));

    return responseFields(fields.getFieldDescriptors());
  }
}

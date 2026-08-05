package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.project.dto.summary.MyProjectSummary;
import com.skkil.sync.project.model.JoinPolicy;
import com.skkil.sync.project.model.Role;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class MyProjectSummarySnippets {

  public static MyProjectSummary getMyProjectSummary() {
    return MyProjectSummary.builder()
        .handle("my-project")
        .name("나의 프로젝트")
        .description("프로젝트 설명")
        .website("https://example.com")
        .isPublic(false)
        .joinPolicy(JoinPolicy.INVITE)
        .followerCount(3)
        .iconUrl("https://example.com/icon.png")
        .role(Role.ADMIN)
        .isOwner(true)
        .memberCount(8)
        .unresolvedQuestionCount(2)
        .build();
  }

  public static List<FieldDescriptor> getMyProjectSummaryFields(String prefix) {
    return List.of(
        fieldWithPath(prefix + "handle").type(JsonFieldType.STRING).description("프로젝트 핸들"),
        fieldWithPath(prefix + "name").type(JsonFieldType.STRING).description("프로젝트 이름"),
        fieldWithPath(prefix + "description")
            .type(JsonFieldType.STRING)
            .optional()
            .description("프로젝트 설명"),
        fieldWithPath(prefix + "website")
            .type(JsonFieldType.STRING)
            .optional()
            .description("프로젝트 웹사이트"),
        fieldWithPath(prefix + "isPublic").type(JsonFieldType.BOOLEAN).description("공개 여부"),
        fieldWithPath(prefix + "joinPolicy")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("프로젝트 참여 정책")
            .attributes(RestDocsUtils.getEnumAttributes(JoinPolicy.class)),
        fieldWithPath(prefix + "followerCount")
            .type(JsonFieldType.NUMBER)
            .description("프로젝트 팔로워 수"),
        fieldWithPath(prefix + "iconUrl")
            .type(JsonFieldType.STRING)
            .optional()
            .description("프로젝트 아이콘 URL"),
        fieldWithPath(prefix + "role")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("현재 사용자의 프로젝트 역할")
            .attributes(RestDocsUtils.getEnumAttributes(Role.class)),
        fieldWithPath(prefix + "isOwner")
            .type(JsonFieldType.BOOLEAN)
            .description("현재 사용자의 프로젝트 소유자 여부"),
        fieldWithPath(prefix + "memberCount").type(JsonFieldType.NUMBER).description("프로젝트 멤버 수"),
        fieldWithPath(prefix + "unresolvedQuestionCount")
            .type(JsonFieldType.NUMBER)
            .description("공개된 미해결 질문 수"));
  }
}

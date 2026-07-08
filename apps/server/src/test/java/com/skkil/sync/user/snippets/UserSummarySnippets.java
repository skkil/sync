package com.skkil.sync.user.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.skkil.sync.user.dto.summary.UserSummary;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class UserSummarySnippets {

  public static UserSummary getUserSummary() {
    return UserSummary.builder()
        .handle("john-doe")
        .name("John Doe")
        .profileImageUrl("https://example.com/john.png")
        .build();
  }

  public static List<FieldDescriptor> getUserSummaryFields(String prefix) {
    return List.of(
        fieldWithPath(prefix + "handle").type(JsonFieldType.STRING).description("유저 핸들"),
        fieldWithPath(prefix + "name").type(JsonFieldType.STRING).description("유저 이름"),
        fieldWithPath(prefix + "profileImageUrl")
            .type(JsonFieldType.STRING)
            .description("유저 프로필 이미지 URL")
            .optional());
  }
}

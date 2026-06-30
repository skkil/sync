package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.OffsetPaginationResponseSnippets;
import com.skkil.sync.post.dto.response.GetPostReportsResponse;
import com.skkil.sync.post.model.PostReportReason;
import com.skkil.sync.post.model.PostReportStatus;
import com.skkil.sync.post.model.PostVisibility;
import java.time.Instant;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetPostReportsResponseSnippets {

  public static GetPostReportsResponse getPostReportsResponse() {
    GetPostReportsResponse.Post post =
        new GetPostReportsResponse.Post(
            1L, "reported-post", "신고된 포스트", "{\"text\":\"신고된 포스트 내용\"}", PostVisibility.HIDDEN);
    GetPostReportsResponse.Reporter reporter =
        new GetPostReportsResponse.Reporter(2L, "reporter", "신고자");
    GetPostReportsResponse.Reviewer reviewer =
        new GetPostReportsResponse.Reviewer(3L, "admin", "관리자");
    GetPostReportsResponse.Report report =
        new GetPostReportsResponse.Report(
            1L,
            post,
            reporter,
            PostReportReason.SPAM,
            "광고성 게시글입니다.",
            PostReportStatus.RESOLVED,
            Instant.EPOCH,
            reviewer,
            Instant.EPOCH,
            "스팸으로 판단했습니다.");

    return new GetPostReportsResponse(OffsetPaginationResponseSnippets.of(List.of(report)));
  }

  public static ResponseFieldsSnippet getPostReportsResponseFields() {
    FieldDescriptors fields =
        OffsetPaginationResponseSnippets.getPaginationResponseFields("reports");

    fields =
        fields.andWithPrefix(
            "reports.content[]",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("신고 ID"),
            fieldWithPath(".reason").type(JsonFieldType.STRING).description("신고 사유"),
            fieldWithPath(".description")
                .type(JsonFieldType.STRING)
                .description("신고 상세 설명")
                .optional(),
            fieldWithPath(".status").type(JsonFieldType.STRING).description("신고 상태"),
            fieldWithPath(".createdAt").type(JsonFieldType.STRING).description("신고 생성 시각"),
            fieldWithPath(".reviewedAt").type(JsonFieldType.STRING).description("검토 시각").optional(),
            fieldWithPath(".resolutionNote")
                .type(JsonFieldType.STRING)
                .description("관리자 처리 메모")
                .optional());

    fields =
        fields.andWithPrefix(
            "reports.content[].post",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("포스트 ID"),
            fieldWithPath(".slug").type(JsonFieldType.STRING).description("포스트 슬러그"),
            fieldWithPath(".title").type(JsonFieldType.STRING).description("포스트 제목").optional(),
            fieldWithPath(".content").type(JsonFieldType.STRING).description("포스트 본문"),
            fieldWithPath(".visibility").type(JsonFieldType.STRING).description("포스트 노출 상태"));

    fields =
        fields.andWithPrefix(
            "reports.content[].reporter",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("신고자 ID"),
            fieldWithPath(".handle").type(JsonFieldType.STRING).description("신고자 핸들"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("신고자 이름"));

    fields =
        fields.andWithPrefix(
            "reports.content[].reviewedBy",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("검토자 ID"),
            fieldWithPath(".handle").type(JsonFieldType.STRING).description("검토자 핸들"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("검토자 이름"));

    return responseFields(fields.getFieldDescriptors());
  }
}

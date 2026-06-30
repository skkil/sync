package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.post.dto.request.ReviewPostReportRequest;
import com.skkil.sync.post.model.PostReportResolution;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class ReviewPostReportRequestSnippets {

  public static ReviewPostReportRequest getReviewPostReportRequest() {
    return new ReviewPostReportRequest(
        PostReportResolution.HIDE_POST, "스팸으로 판단했습니다.", "부적절한 광고성 게시글입니다.");
  }

  public static RequestFieldsSnippet getReviewPostReportRequestFields() {
    return requestFields(
        fieldWithPath("resolution")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("처리 방식")
            .attributes(RestDocsUtils.getEnumAttributes(PostReportResolution.class)),
        fieldWithPath("resolutionNote")
            .type(JsonFieldType.STRING)
            .description("관리자 처리 메모")
            .optional(),
        fieldWithPath("hiddenReason")
            .type(JsonFieldType.STRING)
            .description("포스트 숨김 사유")
            .optional());
  }
}

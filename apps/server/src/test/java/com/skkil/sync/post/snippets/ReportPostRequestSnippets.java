package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.post.dto.request.ReportPostRequest;
import com.skkil.sync.post.model.PostReportReason;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class ReportPostRequestSnippets {

  public static ReportPostRequest getReportPostRequest() {
    return new ReportPostRequest(PostReportReason.SPAM, "광고성 게시글입니다.");
  }

  public static RequestFieldsSnippet getReportPostRequestFields() {
    return requestFields(
        fieldWithPath("reason")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("신고 사유")
            .attributes(RestDocsUtils.getEnumAttributes(PostReportReason.class)),
        fieldWithPath("description").type(JsonFieldType.STRING).description("신고 상세 설명").optional());
  }
}

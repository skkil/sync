package com.skkil.sync.provider.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.response.GetProvidersResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetProvidersResponseSnippets {

  public static GetProvidersResponse getGetProvidersResponse() {
    GetProvidersResponse.Provider provider =
        GetProvidersResponse.Provider.builder()
            .type(ProviderType.COMPANY)
            .id("provider-id")
            .name("Provider Name")
            .isVerified(true)
            .isMaintainer(true)
            .build();

    return new GetProvidersResponse(
        CursorPaginationResponse.<GetProvidersResponse.Provider>builder()
            .content(List.of(provider))
            .hasNext(true)
            .nextCursor("next-cursor")
            .build());
  }

  public static ResponseFieldsSnippet getGetProvidersResponseFieldsSnippet() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("providers");

    fields =
        fields.andWithPrefix(
            "providers.content[]",
            fieldWithPath(".type").type(JsonFieldType.STRING).description("Provider Type"),
            fieldWithPath(".id").type(JsonFieldType.STRING).description("Provider ID"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Provider Name"),
            fieldWithPath(".isVerified")
                .type(JsonFieldType.BOOLEAN)
                .description("Verification Status"),
            fieldWithPath(".isMaintainer")
                .type(JsonFieldType.BOOLEAN)
                .description("Maintainer Status"));

    return responseFields(fields.getFieldDescriptors());
  }
}

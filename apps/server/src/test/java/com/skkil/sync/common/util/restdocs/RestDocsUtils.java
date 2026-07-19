package com.skkil.sync.common.util.restdocs;

import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.snippet.Attributes.Attribute;

public final class RestDocsUtils {

  private RestDocsUtils() {}

  public static final String ENUM_TYPE = "ENUM";

  public static Attribute[] getEnumAttributes(Class<? extends Enum<?>> clazz) {
    return new Attribute[] {new Attribute("enumValues", clazz.getEnumConstants())};
  }

  // SecurityMockMvcRequestPostProcessors.csrf() puts the CSRF token as a form-urlencoded
  // request parameter, which becomes the entire request body for endpoints that otherwise
  // send none — strip it back out so it doesn't leak into the generated OpenAPI request
  // schema. Pass this as the requestPreprocessor argument of MockMvcRestDocumentationWrapper's
  // document(...) for any body-less mutation endpoint documented with csrf().
  public static OperationPreprocessor removeCsrfFormBody() {
    return new OperationPreprocessor() {
      private final OperationRequestFactory requestFactory = new OperationRequestFactory();

      @Override
      public OperationRequest preprocess(OperationRequest request) {
        return requestFactory.createFrom(request, (byte[]) null);
      }

      @Override
      public OperationResponse preprocess(OperationResponse response) {
        return response;
      }
    };
  }
}

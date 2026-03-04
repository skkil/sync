package com.skkil.sync.user.dto.response;

import com.skkil.sync.user.constant.OAuth2Provider;
import java.util.List;

public record GetOAuth2AccountsResponse(List<OAuth2Account> accounts) {

  public static record OAuth2Account(OAuth2Provider provider) {}
}

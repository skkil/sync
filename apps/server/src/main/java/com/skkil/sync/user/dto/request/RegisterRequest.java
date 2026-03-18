package com.skkil.sync.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public record RegisterRequest(
    @NotNull @Email String email, @NotNull @Size(min = 8, max = 128) String password)
    implements Serializable {

  private static final long serialVersionUID = 1L;
}

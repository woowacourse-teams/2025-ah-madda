package com.ahmadda.infra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuthUserInfoResponse(
        @JsonProperty("email") String email,
        @JsonProperty("name") String name
) {

}

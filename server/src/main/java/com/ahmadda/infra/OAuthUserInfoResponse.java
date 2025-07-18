package com.ahmadda.infra;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuthUserInfoResponse(
        @JsonProperty("email") String email,
        @JsonProperty("name") String name
) {

}

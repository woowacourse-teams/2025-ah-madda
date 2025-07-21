package com.ahmadda.infra.dto;

public record JwtPayload(long memberId, String email, String name) {

}

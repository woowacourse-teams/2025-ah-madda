package com.ahmadda.infra.security;

@FunctionalInterface
public interface RandomCodeGenerator {

    String generate(final int length);
}

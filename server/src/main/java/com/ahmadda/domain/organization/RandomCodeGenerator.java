package com.ahmadda.domain.organization;

@FunctionalInterface
public interface RandomCodeGenerator {

    String generate(final int length);
}

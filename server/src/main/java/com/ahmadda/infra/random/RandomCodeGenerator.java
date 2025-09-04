package com.ahmadda.infra.random;

@FunctionalInterface
public interface RandomCodeGenerator {

    String generate(final int length);
}

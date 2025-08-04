package com.ahmadda.infra.generator;

@FunctionalInterface
public interface RandomCodeGenerator {

    String generate(final int length);
}

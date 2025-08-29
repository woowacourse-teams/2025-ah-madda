package com.ahmadda.annotation;

import io.jeyong.detector.annotation.NPlusOneTest;
import io.jeyong.detector.annotation.NPlusOneTest.Mode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles(profiles = "test")
@NPlusOneTest(Mode.LOGGING)
public @interface IntegrationTest {

}


package io.github.pgatzka.example.test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Annotation;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BeanValidationTest<B> {

    private static Validator validator;

    private static ValidatorFactory factory;

    @BeforeAll
    static void beforeAll() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @MethodSource("validCases") @ParameterizedTest(name = "Bean {0} is valid")
    void validArgumentsTests(B bean) {
        assertThat(validator.validate(bean)).isEmpty();
    }

    @MethodSource("invalidCases") @ParameterizedTest(name = "Bean {0} triggers {1} on field {2}")
    void invalidArgumentTests(B bean, Class<? extends Annotation> constraint, String field) {
        Set<ConstraintViolation<B>> validationResult = validator.validate(bean);

        assertThat(validationResult).isNotEmpty();
        assertThat(validationResult).anyMatch(violation -> violation.getPropertyPath().toString().equals(field)
                && violation.getConstraintDescriptor().getAnnotation().annotationType() == constraint);
    }

    @AfterAll
    static void afterEach() {
        factory.close();
    }

}

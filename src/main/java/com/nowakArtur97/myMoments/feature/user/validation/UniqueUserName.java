package com.nowakArtur97.myMoments.feature.user.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = UniqueUserNameConstraintValidator.class)
@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface UniqueUserName {

    String message() default "User name is already taken.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

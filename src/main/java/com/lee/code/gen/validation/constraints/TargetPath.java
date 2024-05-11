package com.lee.code.gen.validation.constraints;

import com.lee.code.gen.validation.TargetPathValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = TargetPathValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
public @interface TargetPath {

    String message() default "{CVM001}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

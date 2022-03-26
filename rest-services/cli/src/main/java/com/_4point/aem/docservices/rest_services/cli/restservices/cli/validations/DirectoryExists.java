package com._4point.aem.docservices.rest_services.cli.restservices.cli.validations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

@Exists
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Constraint(validatedBy = DirectoryExists.DirectoryExistsConstraint.class)
public @interface DirectoryExists {
	Class<?>[] groups() default {};

    String message() default "Must be a directory.";
    
    Class<? extends Payload>[] payload() default {};
    
    public static class DirectoryExistsConstraint implements ConstraintValidator<DirectoryExists, Path> {

    	@Override
		public boolean isValid(Path value, ConstraintValidatorContext context) {
            return Files.isDirectory(value);
		}
    }
}

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

import javax.validation.Payload;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD, PARAMETER, ANNOTATION_TYPE })
@Constraint(validatedBy = Exists.ExistsConstraint.class)
public @interface Exists {
	Class<?>[] groups() default {};

	String message() default "Must exist.";

	Class<? extends Payload>[] payload() default {};

	public static class ExistsConstraint implements ConstraintValidator<Exists, Path> {

		@Override
		public void initialize(Exists contactNumber) {
		}

		@Override
		public boolean isValid(Path value, ConstraintValidatorContext context) {
			return Files.exists(value);
		}
	}
}

package io.github.brunogutierre.simpleurl.link;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * The annotated string must be an absolute {@code http} or {@code https} URL with a host,
 * at most {@link ShortLink#MAX_TARGET_URL_LENGTH} characters long. {@code null} is valid.
 */
@Documented
@Constraint(validatedBy = HttpUrlValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT })
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpUrl {

	String message() default "must be a valid http or https URL";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}

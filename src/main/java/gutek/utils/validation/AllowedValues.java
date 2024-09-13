package gutek.utils.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the allowed values for a field.
 * It can be used to restrict the field values to a predefined set of strings.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedValues {
    /**
     * Specifies the allowed values for the annotated field.
     *
     * @return Array of allowed string values.
     */
    String[] values();
    /**
     * Specifies the translation key for the error message when the value is not allowed.
     *
     * @return The translation key for the error message.
     *         Defaults to "validation.allowed_values".
     */
    String messageTranslationKey() default "validation.allowed_values";
}

package gutek.utils.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the maximum allowed value for a numeric field.
 * It ensures that the annotated field's value does not exceed the specified maximum.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Max {
    /**
     * The maximum value allowed for the field.
     *
     * @return the maximum allowed value.
     */
    double value();
    /**
     * The key for the translation message to be used when the value exceeds the maximum.
     *
     * The default message key is "validation.max".
     *
     * @return the message translation key.
     */
    String messageTranslationKey() default "validation.max";
}
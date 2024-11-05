package gutek.utils.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the minimum allowed value for a numeric field.
 * It ensures that the annotated field's value is not less than the specified minimum.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Min {
    /**
     * The minimum value allowed for the field.
     *
     * @return the minimum allowed value.
     */
    double value();

    /**
     * The key for the translation message to be used when the value is below the minimum.
     * The default message key is "validation.min".
     *
     * @return the message translation key.
     */
    String messageTranslationKey() default "validation.min";
}
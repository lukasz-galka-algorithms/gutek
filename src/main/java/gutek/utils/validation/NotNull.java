package gutek.utils.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify that a field cannot be null.
 * This is used to enforce that certain fields must be initialized with a non-null value.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {
    /**
     * The key for the translation message to be used when the field is null.
     * The default message key is "validation.not_null".
     *
     * @return the message translation key.
     */
    String messageTranslationKey() default "validation.not_null";
}

package gutek.utils.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify that a field cannot be empty.
 * This is applicable to fields such as Strings or Collections, ensuring they are not null or empty.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmpty {
    /**
     * The key for the translation message to be used when the field is empty.
     *
     * The default message key is "validation.not_empty".
     *
     * @return the message translation key.
     */
    String messageTranslationKey() default "validation.not_empty";
}

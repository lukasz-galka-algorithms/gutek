package gutek.entities.algorithms;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/**
 * Custom annotation used to mark fields as algorithm hyperparameters.
 *
 * This annotation can be applied to fields in classes to indicate that they represent
 * hyperparameters for an algorithm. The annotation includes an optional description
 * in the form of a translation key, which can be used for internationalization.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AlgorithmHiperparameter {
    /**
     * Specifies the translation key for the description of the hyperparameter.
     *
     * This key can be used to retrieve a localized description of the hyperparameter
     * from a resource bundle for internationalization purposes.
     *
     * @return the translation key as a string, defaults to an empty string if not provided.
     */
    String descriptionTranslationKey() default "";
}

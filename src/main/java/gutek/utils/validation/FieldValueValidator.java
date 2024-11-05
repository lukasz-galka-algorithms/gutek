package gutek.utils.validation;

import gutek.entities.algorithms.AlgorithmHiperparameter;
import gutek.services.TranslationService;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Utility class for validating field values against annotations like {@link NotNull}, {@link NotEmpty}, {@link Min}, {@link Max}, and {@link AllowedValues}.
 * It also handles value conversion for numeric fields.
 */
public class FieldValueValidator {

    /**
     * Validates and converts the value of a given field using the field's annotations.
     *
     * @param object             The object containing the field to validate.
     * @param fieldName          The name of the field to validate.
     * @param value              The value to validate and convert.
     * @param translationService The translation service used for localized error messages.
     * @return The converted value, if validation passes.
     * @throws IllegalArgumentException If validation fails.
     */
    public static Object validateAndReturnConverted(Object object, String fieldName, Object value, TranslationService translationService) throws IllegalArgumentException {
        Class<?> clazz = object.getClass();

        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
        field.setAccessible(true);

        if (field.isAnnotationPresent(AlgorithmHiperparameter.class)) {
            fieldName = translationService.getTranslation(field.getAnnotation(AlgorithmHiperparameter.class).descriptionTranslationKey());
        }

        if (field.isAnnotationPresent(NotNull.class)) {
            NotNull notNull = field.getAnnotation(NotNull.class);
            if (value == null) {
                throw new IllegalArgumentException(translationService.getTranslation(notNull.messageTranslationKey()).replace("{field}", fieldName));
            }
        }

        if (field.isAnnotationPresent(NotEmpty.class)) {
            NotEmpty notEmpty = field.getAnnotation(NotEmpty.class);
            if (value == null || value.toString().trim().isEmpty()) {
                throw new IllegalArgumentException(translationService.getTranslation(notEmpty.messageTranslationKey()).replace("{field}", fieldName));
            }
        }

        Object convertedValue = convertValue(value, field.getType(), fieldName, translationService);

        if (field.isAnnotationPresent(Min.class)) {
            Min min = field.getAnnotation(Min.class);
            if (convertedValue instanceof Number) {
                double numericValue = ((Number) convertedValue).doubleValue();
                if (numericValue < min.value()) {
                    throw new IllegalArgumentException(
                            translationService.getTranslation(min.messageTranslationKey())
                                    .replace("{field}", fieldName)
                                    .replace("{value}", String.valueOf(min.value()))
                    );
                }
            }
        }

        if (field.isAnnotationPresent(Max.class)) {
            Max max = field.getAnnotation(Max.class);
            if (convertedValue instanceof Number) {
                double numericValue = ((Number) convertedValue).doubleValue();
                if (numericValue > max.value()) {
                    throw new IllegalArgumentException(
                            translationService.getTranslation(max.messageTranslationKey())
                                    .replace("{field}", fieldName)
                                    .replace("{value}", String.valueOf(max.value()))
                    );
                }
            }
        }

        if (field.isAnnotationPresent(AllowedValues.class)) {
            AllowedValues allowedValues = field.getAnnotation(AllowedValues.class);
            if (convertedValue != null && !Arrays.asList(allowedValues.values()).contains(value.toString())) {
                throw new IllegalArgumentException(
                        translationService.getTranslation(allowedValues.messageTranslationKey()).replace("{field}", fieldName)
                                .replace("{allowed_values}", Arrays.toString(allowedValues.values()))
                );
            }
        }

        return convertedValue;
    }

    /**
     * Converts the value to the target type of the field.
     *
     * @param value              The value to convert.
     * @param targetType         The target type of the field.
     * @param fieldName          The name of the field being validated.
     * @param translationService The translation service used for localized error messages.
     * @return The converted value.
     * @throws IllegalArgumentException If the value cannot be converted.
     */
    private static Object convertValue(Object value, Class<?> targetType, String fieldName, TranslationService translationService) {
        if (value instanceof String stringValue) {
            try {
                if (targetType == Integer.class || targetType == int.class) {
                    return Integer.parseInt(stringValue);
                } else if (targetType == Double.class || targetType == double.class) {
                    return Double.parseDouble(stringValue);
                } else if (targetType == Long.class || targetType == long.class) {
                    return Long.parseLong(stringValue);
                } else if (targetType == Float.class || targetType == float.class) {
                    return Float.parseFloat(stringValue);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(translationService.getTranslation("validation.invalid_value")
                        .replace("{field}", fieldName)
                        .replace("{type}", targetType.getName()));
            }
        }
        return value;
    }
}
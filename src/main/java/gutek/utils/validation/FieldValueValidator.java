package gutek.utils.validation;

import gutek.domain.algorithms.AlgorithmHiperparameter;
import gutek.services.TranslationService;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Utility class for validating field values against annotations like {@link NotNull}, {@link NotEmpty}, {@link Min}, {@link Max}, and {@link AllowedValues}.
 * It also handles value conversion for numeric fields.
 */
public class FieldValueValidator {

    /**
     * Placeholder for field
     */
    private static final String FIELD_PLACEHOLDER = "{field}";

    /**
     * Placeholder for value
     */
    private static final String VALUE_PLACEHOLDER = "{value}";

    /**
     * Placeholder for allowed values
     */
    private static final String ALLOWED_VALUES_PLACEHOLDER = "{allowed_values}";

    /**
     * Placeholder for type
     */
    private static final String TYPE_PLACEHOLDER = "{type}";

    /**
     * Private constructor for hiding public one
     */
    private FieldValueValidator(){}

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
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor(object.getClass(), fieldName);
        if (propertyDescriptor == null) {
            return null;
        }

        String fieldLabel = getFieldLabel(propertyDescriptor.getReadMethod().getDeclaringClass(), fieldName, translationService);

        validateNotNull(propertyDescriptor, value, fieldLabel, translationService);
        validateNotEmpty(propertyDescriptor, value, fieldLabel, translationService);

        Object convertedValue = convertValue(value, propertyDescriptor.getPropertyType(), fieldLabel, translationService);

        validateMin(propertyDescriptor, convertedValue, fieldLabel, translationService);
        validateMax(propertyDescriptor, convertedValue, fieldLabel, translationService);
        validateAllowedValues(propertyDescriptor, convertedValue, fieldLabel, translationService);

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
                        .replace(FIELD_PLACEHOLDER, fieldName)
                        .replace(TYPE_PLACEHOLDER, targetType.getName()));
            }
        }
        return value;
    }

    /**
     * Helper method to retrieve a {@link PropertyDescriptor} for a given field name in a specified class.
     *
     * @param clazz the class containing the field
     * @param fieldName the name of the field for which the {@link PropertyDescriptor} is to be retrieved
     * @return the {@link PropertyDescriptor} for the specified field, or {@code null} if an error occurs during introspection
     */
    private static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String fieldName) {
        try {
            return new PropertyDescriptor(fieldName, clazz);
        } catch (IntrospectionException e) {
            return null;
        }
    }

    /**
     * Helper method to retrieve a {@link Field} by its name from a specified class.
     *
     * @param clazz the class containing the field
     * @param fieldName the name of the field to retrieve
     * @return the {@link Field} object representing the specified field, or {@code null} if the field is not found
     */
    private static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * Retrieves the label for a specified field, using the translation service if the field has a description key.
     * <p>
     * If the field is annotated with {@link AlgorithmHiperparameter}, this method will use the translation key
     * provided by the annotation to fetch the translated label via the {@link TranslationService}.
     * If no translation key is found, the method returns the field's name.
     * </p>
     *
     * @param clazz the class containing the field
     * @param fieldName the name of the field whose label is to be retrieved
     * @param translationService the translation service used to fetch the translated label
     * @return the translated label if a description key is present, or the field name if not
     */
    private static String getFieldLabel(Class<?> clazz, String fieldName, TranslationService translationService) {
        Field field = getField(clazz, fieldName);
        if (field != null && field.isAnnotationPresent(AlgorithmHiperparameter.class)) {
            String descriptionKey = field.getAnnotation(AlgorithmHiperparameter.class).descriptionTranslationKey();
            return translationService.getTranslation(descriptionKey);
        }
        return fieldName;
    }

    /**
     * Validates that the specified value is not null if the field is annotated with {@link NotNull}.
     *
     * @param propertyDescriptor  Descriptor for the property being validated.
     * @param value               The value to validate.
     * @param fieldLabel          The label of the field, used in error messages.
     * @param translationService  Service used for obtaining localized error messages.
     * @throws IllegalArgumentException if the value is null and the field is annotated with {@link NotNull}.
     */
    private static void validateNotNull(PropertyDescriptor propertyDescriptor, Object value, String fieldLabel, TranslationService translationService) {
        Field field = getField(propertyDescriptor.getReadMethod().getDeclaringClass(), propertyDescriptor.getName());
        if (field != null && field.isAnnotationPresent(NotNull.class) && value == null) {
            String message = translationService.getTranslation(field.getAnnotation(NotNull.class).messageTranslationKey());
            throw new IllegalArgumentException(message.replace(FIELD_PLACEHOLDER, fieldLabel));
        }
    }

    /**
     * Validates that the specified value is not empty if the field is annotated with {@link NotEmpty}.
     *
     * @param propertyDescriptor  Descriptor for the property being validated.
     * @param value               The value to validate.
     * @param fieldLabel          The label of the field, used in error messages.
     * @param translationService  Service used for obtaining localized error messages.
     * @throws IllegalArgumentException if the value is empty and the field is annotated with {@link NotEmpty}.
     */
    private static void validateNotEmpty(PropertyDescriptor propertyDescriptor, Object value, String fieldLabel, TranslationService translationService) {
        Field field = getField(propertyDescriptor.getReadMethod().getDeclaringClass(), propertyDescriptor.getName());
        if (field != null && field.isAnnotationPresent(NotEmpty.class)) {
            NotEmpty notEmpty = field.getAnnotation(NotEmpty.class);
            if (value == null || value.toString().trim().isEmpty()) {
                String message = translationService.getTranslation(notEmpty.messageTranslationKey());
                throw new IllegalArgumentException(message.replace(FIELD_PLACEHOLDER, fieldLabel));
            }
        }
    }

    /**
     * Validates that the specified value is above the minimum if the field is annotated with {@link Min}.
     *
     * @param propertyDescriptor  Descriptor for the property being validated.
     * @param value               The value to validate.
     * @param fieldLabel          The label of the field, used in error messages.
     * @param translationService  Service used for obtaining localized error messages.
     * @throws IllegalArgumentException if the value is below the minimum specified by the {@link Min} annotation.
     */
    private static void validateMin(PropertyDescriptor propertyDescriptor, Object value, String fieldLabel, TranslationService translationService) {
        Field field = getField(propertyDescriptor.getReadMethod().getDeclaringClass(), propertyDescriptor.getName());
        if (field != null && field.isAnnotationPresent(Min.class) && value instanceof Number number) {
            Min min = field.getAnnotation(Min.class);
            double numericValue = number.doubleValue();
            if (numericValue < min.value()) {
                String message = translationService.getTranslation(min.messageTranslationKey());
                throw new IllegalArgumentException(message.replace(FIELD_PLACEHOLDER, fieldLabel).replace(VALUE_PLACEHOLDER, String.valueOf(min.value())));
            }
        }
    }

    /**
     * Validates that the specified value is below the maximum if the field is annotated with {@link Max}.
     *
     * @param propertyDescriptor  Descriptor for the property being validated.
     * @param value               The value to validate.
     * @param fieldLabel          The label of the field, used in error messages.
     * @param translationService  Service used for obtaining localized error messages.
     * @throws IllegalArgumentException if the value is above the maximum specified by the {@link Max} annotation.
     */
    private static void validateMax(PropertyDescriptor propertyDescriptor, Object value, String fieldLabel, TranslationService translationService) {
        Field field = getField(propertyDescriptor.getReadMethod().getDeclaringClass(), propertyDescriptor.getName());
        if (field != null && field.isAnnotationPresent(Max.class) && value instanceof Number number) {
            Max max = field.getAnnotation(Max.class);
            double numericValue = number.doubleValue();
            if (numericValue > max.value()) {
                String message = translationService.getTranslation(max.messageTranslationKey());
                throw new IllegalArgumentException(message.replace(FIELD_PLACEHOLDER, fieldLabel).replace(VALUE_PLACEHOLDER, String.valueOf(max.value())));
            }
        }
    }

    /**
     * Validates that the specified value is within the allowed values if the field is annotated with {@link AllowedValues}.
     *
     * @param propertyDescriptor  Descriptor for the property being validated.
     * @param value               The value to validate.
     * @param fieldLabel          The label of the field, used in error messages.
     * @param translationService  Service used for obtaining localized error messages.
     * @throws IllegalArgumentException if the value is not within the allowed values specified by the {@link AllowedValues} annotation.
     */
    private static void validateAllowedValues(PropertyDescriptor propertyDescriptor, Object value, String fieldLabel, TranslationService translationService) {
        Field field = getField(propertyDescriptor.getReadMethod().getDeclaringClass(), propertyDescriptor.getName());
        if (field != null && field.isAnnotationPresent(AllowedValues.class)) {
            AllowedValues allowedValues = field.getAnnotation(AllowedValues.class);
            if (value != null && !Arrays.asList(allowedValues.values()).contains(value.toString())) {
                String message = translationService.getTranslation(allowedValues.messageTranslationKey());
                throw new IllegalArgumentException(message.replace(FIELD_PLACEHOLDER, fieldLabel).replace(ALLOWED_VALUES_PLACEHOLDER, Arrays.toString(allowedValues.values())));
            }
        }
    }
}
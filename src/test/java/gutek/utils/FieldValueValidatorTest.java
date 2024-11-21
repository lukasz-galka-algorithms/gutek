package gutek.utils;

import gutek.services.TranslationService;
import gutek.utils.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FieldValueValidatorTest {

    private TranslationService mockTranslationService;

    @BeforeEach
    void setUp() {
        mockTranslationService = mock(TranslationService.class);
        when(mockTranslationService.getTranslation(anyString())).thenReturn("Translated message");
    }

    @Test
    void testValidateAndReturnConverted_NotNullAnnotation() {
        // Arrange
        TestObject testObject = new TestObject();
        String fieldName = "notNullField";
        Object value = "Test";

        // Act
        Object result = FieldValueValidator.validateAndReturnConverted(testObject, fieldName, value, mockTranslationService);

        // Assert
        assertEquals(value, result);
    }

    @Test
    void testValidateAndReturnConverted_NullValueThrowsException() {
        // Arrange
        TestObject testObject = new TestObject();
        String fieldName = "notNullField";
        Object value = null;

        when(mockTranslationService.getTranslation("field.not_null"))
                .thenReturn("Field {field} cannot be null");
        when(mockTranslationService.getTranslation("field.custom_label"))
                .thenReturn("notNullField");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> FieldValueValidator.validateAndReturnConverted(testObject, fieldName, value, mockTranslationService));
        assertTrue(exception.getMessage().contains("Field notNullField cannot be null"));
    }

    @Test
    void testValidateAndReturnConverted_ConvertValidNumber() {
        // Arrange
        TestObject testObject = new TestObject();
        String fieldName = "intField";
        Object value = "42";

        // Act
        Object result = FieldValueValidator.validateAndReturnConverted(testObject, fieldName, value, mockTranslationService);

        // Assert
        assertEquals(42, result);
    }

    @Test
    void testValidateAndReturnConverted_InvalidNumberThrowsException() {
        // Arrange
        TestObject testObject = new TestObject();
        String fieldName = "intField";
        Object value = "Invalid";

        when(mockTranslationService.getTranslation("validation.invalid_value"))
                .thenReturn("Invalid value for field {field}");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> FieldValueValidator.validateAndReturnConverted(testObject, fieldName, value, mockTranslationService));
        assertTrue(exception.getMessage().contains("Invalid value for field intField"));
    }

    @Test
    void testValidateAndReturnConverted_MinValidationPasses() {
        // Arrange
        TestObject testObject = new TestObject();
        String fieldName = "minField";
        Object value = "10";

        // Act
        Object result = FieldValueValidator.validateAndReturnConverted(testObject, fieldName, value, mockTranslationService);

        // Assert
        assertEquals(10, result);
    }

    @Test
    void testValidateAndReturnConverted_MinValidationFails() {
        // Arrange
        TestObject testObject = new TestObject();
        String fieldName = "minField";
        Object value = "5";

        when(mockTranslationService.getTranslation("field.min"))
                .thenReturn("Field {field} must be at least {value}");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> FieldValueValidator.validateAndReturnConverted(testObject, fieldName, value, mockTranslationService));
        assertTrue(exception.getMessage().contains("Field minField must be at least 10"));
    }

    @Test
    void testValidateAndReturnConverted_MaxValidationFails() {
        // Arrange
        TestObject testObject = new TestObject();
        String fieldName = "maxField";
        Object value = "15";

        when(mockTranslationService.getTranslation("field.max"))
                .thenReturn("Field {field} must be at most {value}");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> FieldValueValidator.validateAndReturnConverted(testObject, fieldName, value, mockTranslationService));
        assertTrue(exception.getMessage().contains("Field maxField must be at most 10"));
    }

    @Test
    void testValidateAndReturnConverted_AllowedValuesFails() {
        // Arrange
        TestObject testObject = new TestObject();
        String fieldName = "allowedValuesField";
        Object value = "Invalid";

        when(mockTranslationService.getTranslation("field.allowed_values"))
                .thenReturn("Field {field} must be one of {allowed_values}");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> FieldValueValidator.validateAndReturnConverted(testObject, fieldName, value, mockTranslationService));
        assertTrue(exception.getMessage().contains("Field allowedValuesField must be one of [Allowed, Values]"));
    }

    @Test
    void testValidateAndReturnConverted_AllowedValuesPasses() {
        // Arrange
        TestObject testObject = new TestObject();
        String fieldName = "allowedValuesField";
        Object value = "Allowed";

        // Act
        Object result = FieldValueValidator.validateAndReturnConverted(testObject, fieldName, value, mockTranslationService);

        // Assert
        assertEquals(value, result);
    }

    @Test
    void testValidateAndReturnConverted_NotEmptyAnnotation_ValidValue() {
        // Arrange
        TestObject testObject = new TestObject();
        String fieldName = "notEmptyField";
        String value = "Valid Value";

        when(mockTranslationService.getTranslation("field.not_empty")).thenReturn("Field must not be empty");

        // Act
        Object result = FieldValueValidator.validateAndReturnConverted(testObject, fieldName, value, mockTranslationService);

        // Assert
        assertEquals(value, result);
    }

    @Test
    void testValidateAndReturnConverted_NotEmptyAnnotation_EmptyValue() {
        // Arrange
        TestObject testObject = new TestObject();
        String fieldName = "notEmptyField";
        String value = "";

        when(mockTranslationService.getTranslation("field.not_empty")).thenReturn("Field must not be empty");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                FieldValueValidator.validateAndReturnConverted(testObject, fieldName, value, mockTranslationService)
        );
        assertEquals("Field must not be empty", exception.getMessage());
    }

    @Test
    void testValidateAndReturnConverted_NotEmptyAnnotation_NullValue() {
        // Arrange
        TestObject testObject = new TestObject();
        String fieldName = "notEmptyField";
        String value = null;

        when(mockTranslationService.getTranslation("field.not_empty")).thenReturn("Field must not be empty");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                FieldValueValidator.validateAndReturnConverted(testObject, fieldName, value, mockTranslationService)
        );
        assertEquals("Field must not be empty", exception.getMessage());
    }
}

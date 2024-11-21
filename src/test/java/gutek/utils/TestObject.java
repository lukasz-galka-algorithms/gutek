package gutek.utils;

import gutek.domain.algorithms.AlgorithmHiperparameter;
import gutek.utils.validation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestObject {
    @AlgorithmHiperparameter(descriptionTranslationKey = "field.custom_label")
    @NotNull(messageTranslationKey = "field.not_null")
    private String notNullField;

    @NotEmpty(messageTranslationKey = "field.not_empty")
    private String notEmptyField;

    @Min(value = 10, messageTranslationKey = "field.min")
    private int minField;

    @Max(value = 10, messageTranslationKey = "field.max")
    private int maxField;

    @AllowedValues(values = {"Allowed", "Values"}, messageTranslationKey = "field.allowed_values")
    private String allowedValuesField;

    private int intField;
}
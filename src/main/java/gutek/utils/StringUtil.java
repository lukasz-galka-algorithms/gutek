package gutek.utils;

/**
 * Utility class for common string manipulation operations.
 */
public class StringUtil {
    /**
     * Capitalizes the first letter of the given string and converts the remaining characters to lowercase.
     * <p>
     * This method ensures consistent capitalization, which is especially useful for display purposes where
     * only the first letter should be capitalized.
     * </p>
     *
     * @param string the string to format with the first letter capitalized
     * @return a new string with the first letter capitalized and the remaining characters in lowercase
     * @throws IllegalArgumentException if the input string is null or empty
     */
    public static String capitalizeFirstLetter(String string) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}

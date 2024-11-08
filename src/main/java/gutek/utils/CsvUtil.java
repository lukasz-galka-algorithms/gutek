package gutek.utils;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling CSV file operations for a list of {@link CardBase} objects.
 * <p>
 * This class provides methods for writing a list of cards to a CSV file and includes
 * support for proper escaping of special characters, such as quotes and newlines.
 * </p>
 */
public class CsvUtil {

    /**
     * Private constructor to prevent instantiation
     */
    private CsvUtil(){}

    /**
     * Delimiter used to separate values in the CSV file.
     */
    private static final String CSV_DELIMITER = ";";

    /**
     * Writes a list of cards to a CSV file.
     * <p>
     * Each card is written as a new line in the CSV file, with the "Front Text" and "Back Text" values
     * separated by the defined delimiter. Special characters, including newlines and quotes, are properly escaped.
     * </p>
     *
     * @param file the file to write to
     * @param cards the list of cards to write
     * @throws IOException if an I/O error occurs during writing
     */
    public static void writeToCsv(File file, List<CardBase> cards) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Front Text" + CSV_DELIMITER + "Back Text");

            for (CardBase card : cards) {
                writer.newLine();
                String frontText = escapeCsvValue(card.getFront());
                String backText = escapeCsvValue(card.getBack());
                writer.write(frontText + CSV_DELIMITER + backText);
            }
        }
    }

    /**
     * Reads a list of cards from a CSV file.
     *
     * @param file the CSV file to read from
     * @param algorithm the algorithm used to create cards
     * @return a list of {@link CardBase} objects
     * @throws IOException if an I/O error occurs or the file format is invalid
     */
    public static List<CardBase> loadFromCsv(File file, RevisionAlgorithm<?> algorithm) throws IOException {
        List<CardBase> cards = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine();
            if (header == null) {
                throw new IOException("The CSV file is empty or missing the header line.");
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(CSV_DELIMITER, -1);

                if (values.length < 2) {
                    throw new IOException("Invalid CSV format: each line must contain two values.");
                }

                String front = unescapeCsvValue(values[0]);
                String back = unescapeCsvValue(values[1]);

                CardBase card = algorithm.createNewCard(front, back);
                cards.add(card);
            }
        }

        return cards;
    }

    /**
     * Escapes a value for CSV format, handling quotes and newlines.
     * <p>
     * If the value contains special characters (such as quotes, the CSV delimiter, or newlines),
     * it is enclosed in quotes. Any quotes within the value are doubled to comply with the CSV format.
     * </p>
     *
     * @param value the value to escape
     * @return the escaped value, properly formatted for CSV
     */
    private static String escapeCsvValue(String value) {
        if (value.contains("\"") || value.contains(CSV_DELIMITER) || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Unescapes a CSV-formatted value, removing surrounding quotes and doubling internal quotes.
     * <p>
     * This method reverses the effect of {@link #escapeCsvValue(String)}, restoring the original value.
     * </p>
     *
     * @param value the CSV-formatted value to unescape
     * @return the original, unescaped value
     */
    private static String unescapeCsvValue(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value.replace("\"\"", "\"");
    }
}

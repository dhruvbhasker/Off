package fileutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import au.com.bytecode.opencsv.CSVReader;

public class ParseTemplate {

    // Method to get the template list
    public List<ExtractTemplate> getTemplateList(File file) throws IOException {
        return parseCSVFile(file);
    }

    // Method to get mismatch CSV
    public List<ExtractTemplate> getMismatchCSV(File file) throws IOException {
        return parseCSVFile(file);
    }

    // Common method to parse CSV file (used by both getTemplateList and getMismatchCSV)
    private List<ExtractTemplate> parseCSVFile(File file) throws IOException {
        List<ExtractTemplate> fieldsList = new ArrayList<>();

        // CSVReader to read the file with "Shift-JIS" encoding and using ',' and '\'' as separators
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(new FileInputStream(file), Charset.forName("Shift-JIS")), ',', '\'', 1)) {

            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length != 4) {
                    throw new RuntimeException("Unexpected number of entries: " + row.length);
                }

                // Converting bytes to string using "Shift-JIS" encoding
                byte[] byteColumnName = row[0].getBytes("Shift-JIS");
                String strColumnName = new String(byteColumnName, "Shift-JIS");

                // Adding the parsed row to the list
                fieldsList.add(new ExtractTemplate(strColumnName, row[1], row[2], row[3]));
            }
        }

        return fieldsList;
    }
}

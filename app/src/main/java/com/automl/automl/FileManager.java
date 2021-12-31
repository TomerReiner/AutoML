package com.automl.automl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * This class manages the reading of the file.
 */
public class FileManager {

    public static final String SPLIT = ",";

    public FileManager() {
    }

    /**
     * This function downloads a .csv file from the internet.
     * @param filename A URL for the file.
     * @return The file.
     */
    public BufferedReader getFile(String filename) throws IOException {
        URL url = new URL(filename);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream is = connection.getInputStream();
        return new BufferedReader(new InputStreamReader(is));
    }

    /**
     * This function reads a file into a {@link HashMap}. The keys in the map
     * will be the column names. If a column name is absent, then it will be named by its index
     * in the column list. The values in the map will be the corresponding values in each column.
     * @param reader The file.
     * @return An organized {@link HashMap} with the values.
     */
    public HashMap<String, ArrayList<String>> readFile(BufferedReader reader) throws IOException {
        HashMap<String, ArrayList<String>> map = new HashMap<>();

        String line = reader.readLine();
        String[] columnNames = line.split(SPLIT); // Get the column name rows.

        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].length() == 0) // If the column name is NA.
                map.put("" + i, new ArrayList<>());
            else
                map.put(columnNames[i], new ArrayList<>());
        }

        while ((line = reader.readLine()) != null) { // Append all the values to the HashMap.
            String[] values = line.split(SPLIT);

            for (int i = 0; i < values.length; i++) {
                if (i < map.size())
                    map.get(map.keySet().toArray()[i]).add(values[i]);
            }
        }
        return map;
    }
}

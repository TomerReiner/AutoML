package com.automl.automl;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class manages the reading of the file.
 */
public class FileManager extends AsyncTask<String, Void, Void> {


    private static final String SPLIT = ",";

    private HashMap<String, ArrayList<String>> dataset;

    public FileManager(HashMap<String, ArrayList<String>> dataset) {
        this.dataset = dataset;
    }

    /**
     * This function reads a file into a {@link HashMap}. The keys in the map
     * will be the column names. If a column name is absent, then it will be named by its index
     * in the column list. The values in the map will be the corresponding values in each column.
     * @param reader The file.
     */
    private void readFile(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        String[] columnNames = line.split(SPLIT); // Get the column name rows.

        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].length() == 0) // If the column name is NA.
                this.dataset.put("" + i, new ArrayList<>());
            else
                this.dataset.put(columnNames[i], new ArrayList<>());
        }

        while ((line = reader.readLine()) != null) { // Append all the values to the HashMap.
            String[] values = line.split(SPLIT);

            for (int i = 0; i < values.length; i++) {
                if (i < this.dataset.size())
                    this.dataset.get(this.dataset.keySet().toArray()[i]).add(values[i]);
            }
        }
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream is = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            readFile(reader);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, ArrayList<String>> getDataset() {
        return this.dataset;
    }
}

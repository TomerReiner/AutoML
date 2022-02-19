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
import java.util.Set;

/**
 * This class manages the reading of the file.
 */
public class FileManager extends AsyncTask<String, Void, Void> {


    private static final String SPLIT = ",";

    private HashMap<String, ArrayList<String>> dataset;
    private HashMap<String, ArrayList<String>> removedColumns; // This variable will contain the removed columns to restore them if the user would like to.

    public FileManager(HashMap<String, ArrayList<String>> dataset) {
        this.dataset = dataset;
        this.removedColumns = new HashMap<>();
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

    /**
     * This function removes a columns from the dataset.
     * @param column The column that the user would like to remove.
     */
    public void removeColumn(String column) {
        this.removedColumns.put(column, this.dataset.get(column)); // Save the deleted column
        this.dataset.remove(column);
    }

    public HashMap<String, ArrayList<String>> getDataset() {
        return this.dataset;
    }

    /**
     * This function restores a removed column in the dataset.
     */
    public void restoreColumn() {
        Set<String> removedColumns = this.removedColumns.keySet();
        String[] arr = removedColumns.toArray(new String[0]);

        String lastColumnRemoved = arr[arr.length - 1];

        this.dataset.put(lastColumnRemoved, this.removedColumns.get(lastColumnRemoved));
        this.removedColumns.remove(lastColumnRemoved);
    }

    /**
     * @return The columns of the dataset (i.e., {@link HashMap#keySet()} as an array.
     * Before passing the data to the Python code, the data must be converted into an array.
     */
    public String[] getColumns() {
        return this.dataset.keySet().toArray(new String[0]);
    }
// TODO - make sure there are a least two columns in the dataset.

    /**
     * This function returns an array of arrays with all the data in {@link #dataset}.
     * @return The data in the form of arrays.
     */
    public Object[][] getData() {
        String[] columns = this.getColumns();

        Object[][] values = new Object[columns.length][this.dataset.get(columns[0]).size()];

        for (int i = 0; i < columns.length; i++)
            values[i] = this.dataset.get(columns[i]).toArray();
        return values;
    }

}

package com.automl.automl;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * This class manages the reading of the file.
 */
public class FileManager extends AsyncTask<String, Void, Void> implements Serializable {


    private static final String SPLIT = ",";

    private final HashMap<String, ArrayList<String>> dataset;
    private final HashMap<String, ArrayList<String>> removedColumns; // This variable will contain the removed columns to restore them if the user would like to.

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

        ArrayList<ArrayList<String>> data = new ArrayList<>();

        for (int i = 0; i < columnNames.length; i++)
            data.add(new ArrayList<>());


        while ((line = reader.readLine()) != null) { // Append all the values to the HashMap.
            String[] values = line.split(SPLIT);

            if (values.length < columnNames.length) { // If there are less values in the line than the number of columns we will fill the values with null.
                for (int i = 0; i < values.length; i++)
                    Objects.requireNonNull(data.get(i).add(values[i]));

                for (int i = values.length; i < columnNames.length; i++) {
                    Objects.requireNonNull(data.get(i)).add(null);
                }
            }
            else if (values.length == columnNames.length) { // There are exactly n values in the line.
                for (int i = 0; i < values.length; i++)
                    Objects.requireNonNull(data.get(i)).add(values[i]);

            } // If there are more values in the line we will not read it to prevent losing values.
        }

        for (int i = 0; i < columnNames.length; i++) // Add all the data.
            this.dataset.put(columnNames[i], data.get(i));
        return;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
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
     * @param columnName The name of the column that we want to remove.
     */
    public void restoreColumn(String columnName) {
        this.dataset.put(columnName, this.removedColumns.get(columnName));
        this.removedColumns.remove(columnName);
    }

    /**
     * @return The columns of the dataset (i.e., {@link HashMap#keySet()} as an array.
     * Before passing the data to the Python code, the data must be converted into an array.
     */
    public String[] getColumns() {
        return this.dataset.keySet().toArray(new String[0]);
    }

    /**
     * This function returns an array of arrays with all the data in {@link #dataset}.
     * @return The data in the form of arrays.
     */
    public Object[][] getData() {
        String[] columns = this.getColumns();

        Object[][] values = new Object[columns.length][Objects.requireNonNull(this.dataset.get(columns[0])).size()];

        for (int i = 0; i < columns.length; i++)
            values[i] = Objects.requireNonNull(this.dataset.get(columns[i])).toArray();
        return values;
    }

    /**
     * This function checks if the dataset is empty.
     * @return <code>true</code> if the dataset is empty, <code>false</code> otherwise.
     */
    public boolean isDatasetEmpty() {
        return this.dataset.get(getColumns()[0]).size() == 0; // If one or more of the columns are empty then the dataset is empty.
    }
}

package com.automl.automl;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This class is a template class for displaying an ML Model in {@link MyModelsActivity}.
 * Attributes to be displayed:
 * The ML Model name ({@link MLModel#getType()}), The ML Model attributes ({@link MLModel#getAttributes()}),
 * Number of columns in the dataset, number of rows in the dataset and the model's score on the testing data.
 */
public class MLModelDisplay implements Serializable {

    private String type;
    private final HashMap<String, Object> attributes;
    private final String columns; // Number of columns in the dataset.
    private final String rows; // Number of rows in the dataset.
    private final String score; // The score of the ML Model.

    public MLModelDisplay(MLModel mlModel, String columns, String rows, String score) {
        this.type = mlModel.getType();
        this.attributes = mlModel.getAttributes();
        this.columns = columns;
        this.rows = rows;
        this.score = score;
    }

    /**
     * This function converts a {@link HashMap} into a {@link MLModelDisplay}.
     * This method is used since the database returns the ML Models in a {@link HashMap}, and it should be converted
     * to the desired type before displaying it in {@link MyModelsActivity}
     * @param map The ml model in a map.
     * @return The ML Model
     */
    public static MLModelDisplay fromMap(HashMap<String, Object> map) {
        String type = (String) map.get("type");
        String columns = (String) map.get("columns");
        String rows = (String) map.get("rows");
        String score = (String) map.get("score");

        if (map.containsKey("attributes")) {
            HashMap<String, Object> attributes = (HashMap<String, Object>) map.get("attributes");
            return new MLModelDisplay(new MLModel(type, attributes), columns, rows, score);
        }
        else
            return new MLModelDisplay(new MLModel(type, new HashMap<>()), columns, rows, score);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumns() {
        return columns;
    }

    public String getRows() {
        return rows;
    }

    public String getScore() {
        return score;
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }


    @NonNull
    @Override
    public String toString() {
        return "MLModelDisplay{" +
                "type='" + type + '\'' +
                ", attributes=" + attributes +
                ", columns='" + columns + '\'' +
                ", rows='" + rows + '\'' +
                ", score='" + score + '\'' +
                '}';
    }
}

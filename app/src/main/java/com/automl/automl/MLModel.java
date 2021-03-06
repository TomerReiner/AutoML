package com.automl.automl;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This class is a model class for ML model.
 * @see R.layout#select_ml_model_type_dialog
 * @see R.layout#select_classification_model_dialog
 * @see R.layout#select_regression_model_dialog
 */
public class MLModel implements Serializable {

    private String type; // The type of the ML model.
    private HashMap<String, Object> attributes;

    public MLModel(String type, HashMap<String, Object> attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

    @NonNull
    @Override
    public String toString() {
        return "type='" + type + '\'' + ", attributes=" + attributes;
    }
}

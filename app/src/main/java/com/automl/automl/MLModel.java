package com.automl.automl;

import java.util.HashMap;

/**
 * This class is a model class for ML model.
 * @see R.layout#select_ml_model_type_dialog
 * @see R.layout#select_classification_model_dialog
 * @see R.layout#select_regression_model_dialog
 */
public class MLModel {

    private MLModelType mlModelType; // The type of the ML model.
    private HashMap<String, Object> attributes;

    public MLModel(MLModelType mlModelType, HashMap<String, Object> attributes) {
        this.mlModelType = mlModelType;
        this.attributes = attributes;
    }

    public MLModelType getMlModelType() {
        return this.mlModelType;
    }

    public void setMlModelType(MLModelType mlModelType) {
        this.mlModelType = mlModelType;
    }

    public HashMap<String, Object> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }
}

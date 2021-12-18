package com.automl.automl;

import java.util.HashMap;

public class MLModel {

    private MLModelType mlModelType;
    private HashMap<String, Object> attributes;

    public MLModel(MLModelType mlModelType, HashMap<String, Object> attributes) {
        this.mlModelType = mlModelType;
        this.attributes = attributes;
    }

    public MLModelType getMlModelType() {
        return mlModelType;
    }

    public void setMlModelType(MLModelType mlModelType) {
        this.mlModelType = mlModelType;
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }
}

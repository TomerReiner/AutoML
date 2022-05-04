package com.automl.automl;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This class is a model view for a block.
 * A block represents a processing action for the data in the file.
 * @see com.automl.automl.R.string
 */
public class Block implements Serializable {
    private String type; // The type of the block - DA or ML
    private HashMap<String, Object> attributes; // The attributes of the block.

    public Block(String type, HashMap<String, Object> attributes) {
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
        return "Block{" +
                "type='" + type + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}

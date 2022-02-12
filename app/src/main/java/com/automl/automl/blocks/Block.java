package com.automl.automl.blocks;

import java.util.HashMap;

/**
 * This class is a model view for a block.
 * A block represents a processing action for the data in the file.
 * @see com.automl.automl.R.string
 */
public class Block {


    private String type; // The type of the block - DA or ML
    private String actionName; // The name of the block.
    private HashMap<String, Object> attributes; // The attributes of the block.

    public Block(String type, HashMap<String, Object> attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

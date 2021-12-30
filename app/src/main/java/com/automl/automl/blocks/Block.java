package com.automl.automl.blocks;

import java.util.HashMap;

/**
 * This class is a model view for a block.
 * A block represents a processing action for the data in the file.
 * @see com.automl.automl.R.string
 */
public class Block {

    private HashMap<String, Object> attributes; // The attributes of the block.
    private String type; // The type of the block - DA or ML
    private String actionName; // The name of the block.

    public Block(HashMap<String, Object> attributes, String type, String actionName) {
        this.attributes = attributes;
        this.type = type;
        this.actionName = actionName;
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}

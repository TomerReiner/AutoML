package com.automl.automl;

import android.content.Context;

/**
 * This class represents a block.
 * A block is a ML Model or a DA action.
 * Each block will have <code>x</code> text view attributes.
 * 1 for the name of the block (i.e. the DA action name or the ML model name),
 * and <code>x - 1</code> attributes for the DA or ML model.
 * The attributes vary between each ML Model and DA.
 */
public class BlockView {
    private Context context;
}

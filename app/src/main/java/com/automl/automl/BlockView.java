package com.automl.automl;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;

/**
 * This class represents a block.
 * A block is a ML Model or a DA action.
 * Each block will have <code>x</code> text view attributes.
 * 1 for the name of the block (i.e. the DA action name or the ML model name),
 * and <code>x - 1</code> attributes for the DA or ML model.
 * The attributes vary between each ML Model and DA.
 */
public class BlockView {

    private final Context context;private final LinearLayout linearLayout;
    private static int count = 0; // The number of blocks on the screen.

    public BlockView(Context context,  LinearLayout linearLayout) {
        this.context = context;
        this.linearLayout = linearLayout;
    }

    /**
     * This function will create a block with <code>attributes.size()</code> text views. In addition,
     * the block will also have a title to indicate whether it's a DA block or ML block.
     * @param block A block with attributes.
     * @param yColumn optional, the y column name.
     * @see Block
     */
    private void createBlock(Block block, String ... yColumn) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = new TextView(context);
        tvTitle.setLayoutParams(params);

        String title = block.getType(); // The title of the block.
        setAttributesForTextView(tvTitle, title, 24); // Add the title of the block.
        linearLayout.addView(tvTitle);

        HashMap<String, Object> attributes = block.getAttributes();
        TextView tv = new TextView(context);
        tv.setLayoutParams(params);

        if (yColumn.length == 1) { // If its an ML Model block.
            setAttributesForTextView(tv, "y Column: " + yColumn[0] + "\n" + tv.getText().toString(), 16);
        }

        for (String k : attributes.keySet()) {// Add all the attributes to the container.
            if (attributes.get(k) instanceof String[])
                setAttributesForTextView(tv, k + ": " + Arrays.toString((String[]) attributes.get(k)) + "\n" + tv.getText().toString(), 16);
            else
                setAttributesForTextView(tv, k + ": " + attributes.get(k) + "\n" + tv.getText().toString(), 16);
        }

        linearLayout.addView(tv); // Add the text view to the layout
        count += 2;
    }

    /**
     * This function add a block to the activity.
     * @param block A block with attributes.
     * @param yColumn optional, the y column name.
     */
    public void addBlock(Block block, String ... yColumn) {
        System.out.println(yColumn.length);
        createBlock(block, yColumn);
    }

    /**
     * This function removes a block from the screen.
     */
    public void removeBlock() {
        linearLayout.removeViewsInLayout(count - 2, 2); // Remove 2 because we remove to views.
        count -= 2;
    }

    /**
     * This function sets some attributes for a text view.
     * @param tv a {@link TextView} to set its attributes.
     * @param text the text of the {@link TextView}.
     * @param textSize The font size of the text.
     */
    private void setAttributesForTextView(TextView tv, String text, int textSize) {
        tv.setText(text);
        tv.setTextSize(textSize);
    }
}

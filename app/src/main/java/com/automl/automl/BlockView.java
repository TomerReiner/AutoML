package com.automl.automl;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.automl.automl.blocks.Block;

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

    private final Context context;
    private final ScrollView scrollView;
    private LinearLayout linearLayout;

    public BlockView(Context context, ScrollView scrollView, LinearLayout linearLayout) {
        this.context = context;
        this.scrollView = scrollView;
        this.linearLayout = linearLayout;
    }

    /**
     * This function will create a block with <code>attributes.size()</code> text views. In addition,
     * the block will also have a title to indicate whether it's a DA block or ML block.
     * @param block A block with attributes.
     * @see Block
     */
    private void createBlock(Block block) {
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.setMargins(0, 10, 0, 10);

        TextView tvTitle = new TextView(context);
        tvTitle.setLayoutParams(params);

        String title = block.getType() + ": " + block.getActionName();
        setAttributesForTextView(tvTitle, title, 24); // Add the title of the block.

        linearLayout.addView(tvTitle);

        HashMap<String, Object> attributes = block.getAttributes();
        TextView tv = new TextView(context);

        for (String k : attributes.keySet()) { // Add all the attributes to the container.
            tv.setLayoutParams(params);
            setAttributesForTextView(tv, k +": " + attributes.get(k).toString() + "\n", 16);
        }
        linearLayout.addView(tv); // Add the text view to the layout
    }

    /**
     * This function add a block to the activity.
     * @param block A block with attributes.
     */
    public void addBlock(Block block) {
        createBlock(block);
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

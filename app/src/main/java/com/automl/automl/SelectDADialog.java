package com.automl.automl;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.automl.automl.blocks.Block;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * This class will handle the selection of the Data Analysis action.
 * @see R.layout#select_da_dialog
 */
public class SelectDADialog {

    private Context context;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private FileManager fileManager;
    private BlockView blockView;
    private ArrayList<Block> blocks = new ArrayList<>(); // This array list will store all the blocksa for further usage by the python code.

    public static final String[] FILL_NA_ACTIONS = {"Max Value", "Min Value", "Average Value", "Default Value"};

    public SelectDADialog(Context context, ScrollView scrollView, LinearLayout linearLayout, FileManager fileManager) {
        this.context = context;
        this.scrollView = scrollView;
        this.linearLayout = linearLayout;
        this.fileManager = fileManager;
        this.blockView = new BlockView(context, scrollView, linearLayout);
    }

    /**
     * This function creates a dialog where the user will select a data analysis action.
     */
    public void createSelectDADialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.select_da_dialog);
        dialog.setCancelable(true);

        Button btnRemoveColumn = dialog.findViewById(R.id.btnRemoveColumn);
        Button btnNormalizeColumn = dialog.findViewById(R.id.btnNormalizeColumn);
        Button btnEncodeColumn = dialog.findViewById(R.id.btnEncodeColumn);
        Button btnDropNA = dialog.findViewById(R.id.btnDropNA);
        Button btnFillNA = dialog.findViewById(R.id.btnFillNA);

        String[] da = {""};

        btnRemoveColumn.setOnClickListener(view -> {
            da[0] = context.getString(R.string.remove_column);
            createSelectDAConfigurationDialog(dialog,da[0]);
        });

        btnNormalizeColumn.setOnClickListener(view -> {
            da[0] = context.getString(R.string.normalize_column);
            createSelectDAConfigurationDialog(dialog,da[0]);
        });

        btnEncodeColumn.setOnClickListener(view -> {
            da[0] = context.getString(R.string.encode_column);
            createSelectDAConfigurationDialog(dialog,da[0]);
        });

        btnDropNA.setOnClickListener(view -> {
            da[0] = context.getString(R.string.drop_na);
            createSelectDAConfigurationDialog(dialog,da[0]);
        });

        btnFillNA.setOnClickListener(view -> {
            da[0] = context.getString(R.string.fill_na);
            createSelectDAConfigurationDialog(dialog,da[0]);
        });
        dialog.show();
    }

    /**
     * This function creates a dialog where the user will select the configuration for the DA action.
     * For the following options: {@link R.id#btnRemoveColumn}, {@link R.id#btnNormalizeColumn} and {@link R.id#btnEncodeColumn}
     * the user will only select the column for which they would like to apply the action on. For the action {@link R.id#btnDropNA},
     * The user will not select any further configuration because this action works on the entire dataset by default.
     * For the action {@link R.id#btnFillNA}, the user will select another configuration: How to fill the NA values(max value, min value, average value, most common value, default value).
     * @param selectDADialog - The {@link Dialog} that was created in {@link #createSelectDADialog()}. This function will only close it.
     * @param daAction The string name of the action.
     * @see R.string
     * @see #createSelectDADialog()
     */
    private void createSelectDAConfigurationDialog(Dialog selectDADialog, String daAction) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.select_da_configuration_dialog);
        dialog.setCancelable(true);

        Set<String> columns = this.fileManager.getDataset().keySet(); // The columns in the dataset.
        HashMap<String, Object> attributes = new HashMap<>(); // This attributes will store the configuration for the da block.

        attributes.put("action", daAction);

        if (daAction.equals(context.getString(R.string.drop_na))) { // If the user would like to remove NA values.
            attributes.put(context.getString(R.string.column), columns); // Add all the columns to the list.
            Block block = new Block(context.getString(R.string.data_analysis), attributes);
            this.blockView.addBlock(block);
            this.blocks.add(block); // Add the block to the list.
            dialog.dismiss();
            selectDADialog.dismiss();
            return;
        }

        Spinner spinnerSelectColumn = dialog.findViewById(R.id.spinnerSelectColumn);
        Spinner spinnerSelectFillNAOptions = dialog.findViewById(R.id.spinnerSelectFillNAOptions);
        TextView tvFillNAInstructions = dialog.findViewById(R.id.tvFillNAInstructions);
        Button btnSelectDAConfig = dialog.findViewById(R.id.btnSelectDAConfig);

        setSpinnerItems(spinnerSelectColumn, columns.toArray(new String[0]));
        setSpinnerItems(spinnerSelectFillNAOptions, FILL_NA_ACTIONS); // Set the items of the spinners.

        if (daAction.equals(context.getString(R.string.fill_na))) {
            tvFillNAInstructions.setVisibility(View.VISIBLE);
            spinnerSelectFillNAOptions.setVisibility(View.VISIBLE);
        }

        btnSelectDAConfig.setOnClickListener(view -> {
            Block block = null;
            BlockView blockView = null;

            String selectedColumn = (String) spinnerSelectColumn.getItemAtPosition(spinnerSelectColumn.getSelectedItemPosition());
            attributes.put(context.getString(R.string.column), selectedColumn); // Add the relevant column

            if (daAction.equals(context.getString(R.string.fill_na))) { // If the user wants to fill NA values.
                String selectedFillNAAction = (String) spinnerSelectFillNAOptions.getItemAtPosition(spinnerSelectFillNAOptions.getSelectedItemPosition());
                attributes.put(context.getString(R.string.fill_na_method), selectedFillNAAction);
            }

            if (daAction.equals(context.getString(R.string.remove_column))) { // If the user wants to remove a column.
                block = new Block(context.getString(R.string.data_analysis), attributes);
                this.blockView.addBlock(block); // Add the block to the screen. The block will not be added to the list because we will remove the column.

                fileManager.removeColumn(selectedColumn); // Remove the column from the dataset.
                selectDADialog.dismiss();
                dialog.dismiss(); // Dismiss the two dialogs.
                return;
            }

            // If the user has selected another block.
            block = new Block(context.getString(R.string.data_analysis), attributes);
            this.blockView.addBlock(block);
            this.blocks.add(block);

            selectDADialog.dismiss();
            dialog.dismiss();
        });

        dialog.setOnCancelListener(dialogInterface -> { // If the dialog is canceled no block will be created.
            selectDADialog.dismiss();
        });
        dialog.show();
    }

    /**
     * This function sets the items of a spinner.
     * @param spinner The spinner for which we would like to set the selectable items.
     * @param items The options to select in the spinner.
     */
    private void setSpinnerItems(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    /**
     * This function removes the last action from the blocks. If the last action was removing a column,
     * the column will be restored to the dataset.
     * The function will also remove the last block from the screen.
     */
    public void undo() {
        if (this.blocks.size() == 0) // If there are no blocks there is nothing to undo.
            return;

        Block last = this.blocks.get(this.blocks.size() - 1);
        HashMap<String, Object> attributes = last.getAttributes();

        if (attributes.get(context.getString(R.string.column)).equals(context.getString(R.string.remove_column))) { // If the last Data Analysis action was removing a column.
            this.fileManager.restoreColumn();
        }
        this.blocks.remove(this.blocks.size() - 1);
        this.blockView.removeBlock();
    }

}

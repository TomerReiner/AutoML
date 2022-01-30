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
    private ArrayList<Block> blocks = new ArrayList<>(); // This array list will store all the blocksa for further usage by the python code.

    public static final String[] FILL_NA_ACTIONS = {"Max Value", "Min Value", "Average Value", "Default Value"};

    public SelectDADialog(Context context, ScrollView scrollView, LinearLayout linearLayout) {
        this.context = context;
        this.scrollView = scrollView;
        this.linearLayout = linearLayout;
    }

    /**
     * This function creates a dialog where the user will select a data analysis action.
     * @param fileManager The file.
     */
    public void createSelectDADialog(FileManager fileManager) {
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
            createSelectDAConfigurationDialog(dialog,da[0], fileManager);
        });

        btnNormalizeColumn.setOnClickListener(view -> {
            da[0] = context.getString(R.string.normalize_column);
            createSelectDAConfigurationDialog(dialog,da[0], fileManager);
        });

        btnEncodeColumn.setOnClickListener(view -> {
            da[0] = context.getString(R.string.encode_column);
            createSelectDAConfigurationDialog(dialog,da[0], fileManager);
        });

        btnDropNA.setOnClickListener(view -> {
            da[0] = context.getString(R.string.drop_na);
            createSelectDAConfigurationDialog(dialog,da[0], fileManager);
        });

        btnFillNA.setOnClickListener(view -> {
            da[0] = context.getString(R.string.fill_na);
            createSelectDAConfigurationDialog(dialog,da[0], fileManager);
        });
        dialog.show();
    }

    /**
     * This function creates a dialog where the user will select the configuration for the DA action.
     * For the following options: {@link R.id#btnRemoveColumn}, {@link R.id#btnNormalizeColumn} and {@link R.id#btnEncodeColumn}
     * the user will only select the column for which they would like to apply the action on. For the action {@link R.id#btnDropNA},
     * The user will not select any further configuration because this action works on the entire dataset by default.
     * For the action {@link R.id#btnFillNA}, the user will select another configuration: How to fill the NA values(max value, min value, average value, most common value, default value).
     * @param selectDADialog - The {@link Dialog} that was created in {@link #createSelectDADialog(FileManager)}. This function will only close it.
     * @param daAction The string name of the action.
     * @param fileManager The file.
     * @see R.string
     * @see #createSelectDADialog(FileManager)
     */
    private void createSelectDAConfigurationDialog(Dialog selectDADialog, String daAction, FileManager fileManager) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.select_da_configuration_dialog);
        dialog.setCancelable(true);

        Set<String> columns = fileManager.getDataset().keySet(); // The columns in the dataset.
        HashMap<String, Object> map = new HashMap<>(); // This map will store the configuration for the da block.

        if (daAction.equals(context.getString(R.string.drop_na))) { // If the user would like to remove NA values.
            map.put(context.getString(R.string.column), columns); // Add all the columns to the list.
            Block block = new Block(context.getString(R.string.da), daAction, map);
            BlockView blockView = new BlockView(context, scrollView, linearLayout);
            blockView.addBlock(block);
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
            map.put(context.getString(R.string.column), selectedColumn); // Add the relevant column

            if (daAction.equals(context.getString(R.string.fill_na))) { // If the user wants to fill NA values.
                String selectedFillNAAction = (String) spinnerSelectFillNAOptions.getItemAtPosition(spinnerSelectFillNAOptions.getSelectedItemPosition());
                map.put(context.getString(R.string.fill_na), selectedFillNAAction);
            }

            if (daAction.equals(context.getString(R.string.remove_column))) { // If the user wants to fill NA values.
                block = new Block(context.getString(R.string.data_analysis), daAction, map);
                blockView = new BlockView(context, scrollView, linearLayout);
                blockView.addBlock(block); // Add the block to the screen. The block will not be added to the list because we will remove the column.
                fileManager.removeColumn(selectedColumn); // Remove the column from the dataset.
                selectDADialog.dismiss();
                dialog.dismiss(); // Dismiss the two dialogs.
                return;
            }

            block = new Block(context.getString(R.string.data_analysis), daAction, map);
            blockView = new BlockView(context, scrollView, linearLayout);
            blockView.addBlock(block);
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

}

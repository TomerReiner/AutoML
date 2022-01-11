package com.automl.automl;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.automl.automl.blocks.Block;

import java.util.HashMap;
import java.util.Set;

/**
 * This class will handle the selection of the Data Analysis action.
 * @see R.layout#select_da_dialog
 */
public class SelectDADialog {

    private Context context;
    private Block block; // This object will contain the generated block. If the block is empty, then it will be null.


    public static final String[] FILL_NA_ACTIONS = {"Max Value", "Min Value", "Average Value", "Default Value"};

    public SelectDADialog(Context context) {
        this.context = context;
        this.block = null;
    }

    /**
     * This function creates a dialog where the user will select a data analysis action.
     * @param columns The columns in the dataset.
     */
    public void createSelectDADialog(Set<String> columns) {
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
            createSelectDAConfigurationDialog(dialog,da[0], columns);
        });

        btnNormalizeColumn.setOnClickListener(view -> {
            da[0] = context.getString(R.string.normalize_column);
            createSelectDAConfigurationDialog(dialog,da[0], columns);
        });

        btnEncodeColumn.setOnClickListener(view -> {
            da[0] = context.getString(R.string.encode_column);
            createSelectDAConfigurationDialog(dialog,da[0], columns);
        });

        btnDropNA.setOnClickListener(view -> {
            da[0] = context.getString(R.string.drop_na);
            createSelectDAConfigurationDialog(dialog,da[0], columns);
        });

        btnFillNA.setOnClickListener(view -> {
            da[0] = context.getString(R.string.fill_na);
            createSelectDAConfigurationDialog(dialog,da[0], columns);
        });
        dialog.show();
    }

    /**
     * This function creates a dialog where the user will select the configuration for the DA action.
     * For the following options: {@link R.id#btnRemoveColumn}, {@link R.id#btnNormalizeColumn} and {@link R.id#btnEncodeColumn}
     * the user will only select the column for which they would like to apply the action on. For the action {@link R.id#btnDropNA},
     * The user will not select any further configuration because this action works on the entire dataset by default.
     * For the action {@link R.id#btnFillNA}, the user will select another configuration: How to fill the NA values(max value, min value, average value, most common value, default value).
     * @param selectDADialog - The {@link Dialog} that was created in {@link #createSelectDADialog(Set)}. This function will only close it.
     * @param daAction The string name of the action.
     * @param columns A set with the column names.
     * @see R.string
     * @see #createSelectDADialog(Set)
     */
    private void createSelectDAConfigurationDialog(Dialog selectDADialog, String daAction, Set<String> columns) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.select_da_configuration_dialog);
        dialog.setCancelable(true);

        HashMap<String, Object> map = new HashMap<>(); // This map will store the configuration for the da block.

        if (daAction.equals(context.getString(R.string.drop_na))) { // If the user would like to remove NA values.
            map.put(context.getString(R.string.column), columns); // Add all the columns to the list.
            this.block = new Block(map, context.getString(R.string.da), daAction);
            dialog.dismiss();
            selectDADialog.dismiss();
            return;
        }

        Spinner spinnerSelectColumn = dialog.findViewById(R.id.spinnerSelectColumn);
        Spinner spinnerSelectFillNAOptions = dialog.findViewById(R.id.spinnerSelectFillNAOptions);

        Button btnSelectDAConfig = dialog.findViewById(R.id.btnSelectDAConfig);

        setSpinnerItems(spinnerSelectColumn, columns.toArray(new String[0]));
        setSpinnerItems(spinnerSelectFillNAOptions, FILL_NA_ACTIONS); // Set the items of the spinners.

        String selectedColumn = (String) spinnerSelectColumn.getItemAtPosition(spinnerSelectColumn.getSelectedItemPosition());

        map.put(context.getString(R.string.column), selectedColumn);

        if (daAction.equals(context.getString(R.string.fill_na_block))) { // If the user wants to fill NA values.
            spinnerSelectFillNAOptions.setVisibility(View.VISIBLE); // Showing the fill NA options for the user.
            String selectedFillNAAction = (String) spinnerSelectFillNAOptions.getItemAtPosition(spinnerSelectFillNAOptions.getSelectedItemPosition());
            map.put(context.getString(R.string.fill_na_block), selectedFillNAAction);
        }
// TODO - progress bar for 1 second.
        btnSelectDAConfig.setOnClickListener(view -> {
            this.block = new Block(map, context.getString(R.string.da), daAction);
            dialog.dismiss();
            selectDADialog.dismiss();
        });

        dialog.setOnCancelListener(dialogInterface -> { // If the dialog is canceled no block will be created.
            selectDADialog.dismiss();
        });
        dialog.show();
    }

    /**
     * This function sets the items of a spinner.
     * @param spinner The spinner for which we would like to set the selectable items.
     * @param items The items that the user will be able to select in the spinner.
     */
    private void setSpinnerItems(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public Block getBlock() {
        return this.block;
    }
}

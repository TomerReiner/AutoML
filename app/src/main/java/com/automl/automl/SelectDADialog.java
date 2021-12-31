package com.automl.automl;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.automl.automl.blocks.Block;

import java.util.HashMap;

/**
 * This class will handle the selection of the Data Analysis action.
 * @see R.layout#select_da_dialog
 */
public class SelectDADialog {

    private Context context;

    public static final String[] FILL_NA_ACTIONS = {"Max Value", "Min Value", "Average Value", "Default Value"};

    public SelectDADialog(Context context) {
        this.context = context;
    }

    /**
     * This function creates a dialog where the user will select a data analysis action.
     * @return The selected DA action.
     */
    public String createSelectDADialog() {
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
            dialog.dismiss();
        });

        btnNormalizeColumn.setOnClickListener(view -> {
            da[0] = context.getString(R.string.normalize_column);
            dialog.dismiss();
        });

        btnEncodeColumn.setOnClickListener(view -> {
            da[0] = context.getString(R.string.encode_column);
            dialog.dismiss();
        });

        btnDropNA.setOnClickListener(view -> {
            da[0] = context.getString(R.string.drop_na);
            dialog.dismiss();
        });

        btnFillNA.setOnClickListener(view -> {
            da[0] = context.getString(R.string.fill_na);
            dialog.dismiss();
        });

        dialog.show();

        return da[0];
    }

    /**
     * This function creates a dialog where the user will select the configuration for the DA action.
     * For the following options: {@link R.id#btnRemoveColumn}, {@link R.id#btnNormalizeColumn} and {@link R.id#btnEncodeColumn}
     * the user will only select the column for which they would like to apply the action on. For the action {@link R.id#btnDropNA},
     * The user will not select any further configuration because this action works on the entire dataset by default.
     * For the action {@link R.id#btnFillNA}, the user will select another configuration: How to fill the NA values(max value, min value, average value, most common value, default value).
     * @param daAction The string name of the action.
     * @param columns An array with the column names.
     * @return A block with the configuration of the action.
     * @see R.string
     * @see #createSelectDADialog()
     */
    public Block createSelectDAConfigurationDialog(String daAction, String[] columns) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.select_da_configuration_dialog);
        dialog.setCancelable(true);

        if (daAction.equals(context.getString(R.string.drop_na))) {
            HashMap<String, Object> map = new HashMap<>();
            map.put(context.getString(R.string.column), "all");
            return new Block(map, context.getString(R.string.da), daAction);
        }

        Spinner spinnerSelectColumn = dialog.findViewById(R.id.spinnerSelectColumn);
        Spinner spinnerSelectFillNAOptions = dialog.findViewById(R.id.spinnerSelectFillNAOptions);

        setSpinnerItems(spinnerSelectColumn, columns);
        setSpinnerItems(spinnerSelectFillNAOptions, FILL_NA_ACTIONS); // Set the items of the spinners.

        String selectedColumn = (String) spinnerSelectColumn.getItemAtPosition(spinnerSelectColumn.getSelectedItemPosition());

        HashMap<String, Object> map = new HashMap<>();
        map.put(context.getString(R.string.column), selectedColumn);

        if (daAction.equals(context.getString(R.string.fill_na_block))) {
            spinnerSelectFillNAOptions.setVisibility(View.VISIBLE);
            String selectedFillNAAction = (String) spinnerSelectFillNAOptions.getItemAtPosition(spinnerSelectFillNAOptions.getSelectedItemPosition());
            map.put(context.getString(R.string.fill_na_block), selectedFillNAAction);
        }
        return new Block(map, context.getString(R.string.da), daAction);
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

}

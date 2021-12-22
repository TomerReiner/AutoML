package com.automl.automl;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;

/**
 * This class will handle the selection of the Data Analysis action.
 * @see R.layout#select_da_dialog
 */
public class SelectDADialog {

    private Context context;

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
}

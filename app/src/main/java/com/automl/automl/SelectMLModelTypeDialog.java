package com.automl.automl;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;

public class SelectMLModelTypeDialog {

    private Context context;
    private Button btnClassificationModel;
    private Button btnRegressionModel;

    public SelectMLModelTypeDialog(Context context) {
        this.context = context;
    }

    /**
     * This function creates a dialog in which the user will select which type of ML Model he would like to build.
     * There are 2 types of ML Models:
     * Classification Models: Classify which group does the data belong to.
     * For example, predict which type of car the user will buy based on his budget and needs.
     * Regression Models: Predict the value of a data. For example, predict the price of an accommodation
     * based on various parameters such as number of parking places, number of rooms, etc.
     * @return The model that the user has selected.
     */
    public String createSelectMlModelDialog() {
        Dialog dialog = new Dialog(this.context);
        dialog.setContentView(R.layout.select_ml_model_type_dialog);

        btnClassificationModel = dialog.findViewById(R.id.btnClassificationModel);
        btnRegressionModel = dialog.findViewById(R.id.btnRegressionModel);

        btnClassificationModel.setOnClickListener(view -> {

        });

        btnRegressionModel.setOnClickListener(view -> {

        });


        return null;
    }


    private String createSelectClassificationModelDialog() {
        Dialog dialog = new Dialog(this.context);
        dialog.setContentView(R.layout.select_classification_model_dialog);

        Button btnDecisionTreeClassifier = dialog.findViewById(R.id.btnDecisionTreeClassifier);
        Button btnRandomForestClassifier = dialog.findViewById(R.id.btnRandomForestClassifier);
        Button btnKNearestNeighborsClassifier = dialog.findViewById(R.id.btnKNearestNeighborsClassifier);
        Button btnSVC = dialog.findViewById(R.id.btnSVC);
        Button btnNaiveBayes = dialog.findViewById(R.id.btnNaiveBayes);

        return null;
    }


}

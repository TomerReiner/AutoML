package com.automl.automl;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.widget.Button;

public class SelectMLModelDialog {

    private Context context;
    private Button btnClassificationModel;
    private Button btnRegressionModel;

    public SelectMLModelDialog(Context context) {
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

        String[] selectedModel = {""};

        btnClassificationModel.setOnClickListener(view -> {
            selectedModel[0] = createSelectClassificationModelDialog();
            dialog.dismiss();
        });

        btnRegressionModel.setOnClickListener(view -> {
            selectedModel[0] = createSelectRegressionModelDialog();
            dialog.dismiss();
        });
        dialog.show();

        return selectedModel[0];
    }

    /**
     * This function create a dialog where the user will select a classification model.
     * @return The classification model the user has selected.
     */
    private String createSelectClassificationModelDialog() {
        Dialog dialog = new Dialog(this.context);
        dialog.setContentView(R.layout.select_classification_model_dialog);

        Button btnDecisionTreeClassifier = dialog.findViewById(R.id.btnDecisionTreeClassifier);
        Button btnRandomForestClassifier = dialog.findViewById(R.id.btnRandomForestClassifier);
        Button btnKNearestNeighborsClassifier = dialog.findViewById(R.id.btnKNearestNeighborsClassifier);
        Button btnSVC = dialog.findViewById(R.id.btnSVC);
        Button btnNaiveBayes = dialog.findViewById(R.id.btnNaiveBayes);

        String[] s = {""};

        btnDecisionTreeClassifier.setOnClickListener(view -> {
                s[0] = this.context.getString(R.string.decision_tree_classifier);
                dialog.dismiss();
            }
        );

        btnRandomForestClassifier.setOnClickListener(view -> {
                s[0] = this.context.getString(R.string.random_forest_classifier);
                dialog.dismiss();
            }
        );

        btnKNearestNeighborsClassifier.setOnClickListener(view -> {
                s[0] = this.context.getString(R.string.k_nearest_neighbors_classifier);
                dialog.dismiss();
            }
        );

        btnSVC.setOnClickListener(view -> {
                s[0] = this.context.getString(R.string.svc);
                dialog.dismiss();
            }
        );

        btnNaiveBayes.setOnClickListener(view -> {
                s[0] = this.context.getString(R.string.naive_bayes);
                dialog.dismiss();
            }
        );
        dialog.show();

        return s[0];
    }

    /**
     * This function create a dialog where the user will select a regression model.
     * @return The regression model the user has selected.
     */
    private String createSelectRegressionModelDialog() {
        Dialog dialog = new Dialog(this.context);
        dialog.setContentView(R.layout.select_regression_model_dialog);

        Button btnDecisionTreeRegressor = dialog.findViewById(R.id.btnDecisionTreeRegressor);
        Button btnRandomForestRegressor = dialog.findViewById(R.id.btnRandomForestRegressor);
        Button btnLinearRegression = dialog.findViewById(R.id.btnLinearRegression);
        Button btnSVR = dialog.findViewById(R.id.btnSVR);
        Button btnElasticNetCV = dialog.findViewById(R.id.btnElasticNetCV);

        String[] s = {""};

        btnDecisionTreeRegressor.setOnClickListener(view -> {
                s[0] = this.context.getString(R.string.decision_tree_regressor);
                dialog.dismiss();
            }
        );

        btnRandomForestRegressor.setOnClickListener(view -> {
                s[0] = this.context.getString(R.string.random_forest_regressor);
                dialog.dismiss();
            }
        );

        btnLinearRegression.setOnClickListener(view -> {
                s[0] = this.context.getString(R.string.linear_regression);
                dialog.dismiss();
            }
        );

        btnSVR.setOnClickListener(view -> {
                s[0] = this.context.getString(R.string.svr);
                dialog.dismiss();
            }
        );

        btnElasticNetCV.setOnClickListener(view -> {
                s[0] = this.context.getString(R.string.elastic_net_cv);
                dialog.dismiss();
            }
        );

        dialog.show();

        return s[0];
    }
}

package com.automl.automl;

import android.app.Dialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.automl.automl.blocks.Block;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Set;

/**
 * This class handles the creation of the select ML model dialog.
 * @see R.layout#select_ml_model_type_dialog
 * @see R.layout#select_classification_model_dialog
 * @see R.layout#select_regression_model_dialog
 */
public class SelectMLModelDialog {

    public static final String[] KNN_WEIGHTS = {"uniform", "distance"};
    public static final String[] KNN_ALGORITHMS = {"auto", "ball_tree", "kd_tree", "brute"};
    public static final String[] RANDOM_FOREST_MAX_FEATURES = {"auto", "sqrt", "log2"};
    public static final String[] RANDOM_FOREST_CLASSIFIER_CRITERIA = {"gini", "entropy"};
    public static final String[] RANDOM_FOREST_REGRESSOR_CRITERIA = {"auto", "sqrt", "log2"};
    public static final String[] SVM_KERNEL = {"linear", "poly", "rbf", "sigmoid", "precomputed"};

    private Context context; // The context of the activity.
    private BlockView blockView; // A block view where the user will see the ML Model building pipeline.

    /**
     * This variable is used to set {@link ExtendedFloatingActionButton#setClickable(boolean)} to <code>false</code>
     * after an ML Model was chosen, since Adding ML Model to the pipeline is the last step.
     * @see R.id#fabAddDataAnalysisBlock
     */
    private ExtendedFloatingActionButton fabAddDA;

    private String yColumn = null; // The target columns.
    private MLModel mlModel = null;

    public SelectMLModelDialog(Context context, ScrollView scrollView, LinearLayout linearLayout, ExtendedFloatingActionButton fabAddDA) {
        this.context = context;
        this.fabAddDA = fabAddDA;
        this.blockView = new BlockView(context, scrollView, linearLayout);
    }

    /**
     * This function creates a dialog in which the user will select which type of ML Model he would like to build.
     * There are 2 types of ML Models:
     * Classification Models: Classify which group does the data belong to.
     * For example, predict which type of car the user will buy based on his budget and needs.
     * Regression Models: Predict the value of a data. For example, predict the price of an accommodation
     * based on various parameters such as number of parking places, number of rooms, etc.
     * @param columns The columns in the dataset. The user will choose one of them to be the Y column in {@link R.id#spinnerSelectYColumn},
     * and the rest will be the X columns. If the user has selected the Y column to be a regression model, and the ML Model to be a classification model,
     * a warning will be raised, and same if the opposite happens.
     */
    public void createSelectMlModelDialog(Set<String> columns) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.select_ml_model_type_dialog);

        Spinner spinnerSelectYColumn = dialog.findViewById(R.id.spinnerSelectYColumn);
        RadioGroup rgSelectMLModel = dialog.findViewById(R.id.rgSelectMLModel);
        Button btnSelect = dialog.findViewById(R.id.btnSelect);

        setSpinnerItems(spinnerSelectYColumn, columns.toArray(new String[0]));

        btnSelect.setOnClickListener(v -> {

            this.yColumn = (String) spinnerSelectYColumn.getItemAtPosition(spinnerSelectYColumn.getSelectedItemPosition());

            if (rgSelectMLModel.getCheckedRadioButtonId() == R.id.rbClassificationModel) { // If the user wants to create a classification model.
                createSelectClassificationModelDialog();
                dialog.dismiss();
            }
            else if (rgSelectMLModel.getCheckedRadioButtonId() == R.id.rbRegressionModel) { // If the user wants to create a regression model.
                createSelectRegressionModelDialog();
                    dialog.dismiss();
            }
            else // If the user hasn't selected an ml model.
                Toast.makeText(context, "You must select the type of the ML Model", Toast.LENGTH_LONG).show();
        });

        dialog.show();
    }

    /**
     * This function create a dialog where the user will select a classification model.
     */
    private void createSelectClassificationModelDialog() {
        Dialog dialog = new Dialog(this.context);
        dialog.setContentView(R.layout.select_classification_model_dialog);

        Button btnDecisionTreeClassifier = dialog.findViewById(R.id.btnDecisionTreeClassifier);
        Button btnRandomForestClassifier = dialog.findViewById(R.id.btnRandomForestClassifier);
        Button btnKNearestNeighborsClassifier = dialog.findViewById(R.id.btnKNearestNeighborsClassifier);
        Button btnSVC = dialog.findViewById(R.id.btnSVC);
        Button btnNaiveBayes = dialog.findViewById(R.id.btnNaiveBayes);

        btnDecisionTreeClassifier.setOnClickListener(view -> {
            HashMap<String, Object> attributes = new HashMap<>();

            this.mlModel = new MLModel(context.getString(R.string.decision_tree_classifier), attributes);
            Block block = new Block(context.getString(R.string.ml) + ": " + this.mlModel.getType(), this.mlModel.getAttributes());
            this.blockView.addBlock(block); // Add the ML Model to the graph on the screen.
            this.fabAddDA.setClickable(false);
            dialog.dismiss();
        });

        btnRandomForestClassifier.setOnClickListener(view -> {
            createRandomForestConfigDialog(true);
            dialog.dismiss();
        });

        btnKNearestNeighborsClassifier.setOnClickListener(view -> {
            createKNNConfigDialog();
            dialog.dismiss();
        });

        btnSVC.setOnClickListener(view -> {
            createSVMConfigDialog(true);
            dialog.dismiss();
        });

        btnNaiveBayes.setOnClickListener(view -> {
            createNaiveBayesConfigDialog();
            dialog.dismiss();
        });
        dialog.show();
    }

    /**
     * This function create a dialog where the user will select a regression model.
     */
    private void createSelectRegressionModelDialog() {
        Dialog dialog = new Dialog(this.context);
        dialog.setContentView(R.layout.select_regression_model_dialog);

        Button btnDecisionTreeRegressor = dialog.findViewById(R.id.btnDecisionTreeRegressor);
        Button btnRandomForestRegressor = dialog.findViewById(R.id.btnRandomForestRegressor);
        Button btnLinearRegression = dialog.findViewById(R.id.btnLinearRegression);
        Button btnSVR = dialog.findViewById(R.id.btnSVR);
        Button btnElasticNetCV = dialog.findViewById(R.id.btnElasticNetCV);

        HashMap<String, Object> attributes = new HashMap<>();

        btnDecisionTreeRegressor.setOnClickListener(view -> {
            this.mlModel = new MLModel(context.getString(R.string.decision_tree_regressor), attributes);
            Block block = new Block(context.getString(R.string.ml) + ": " + this.mlModel.getType(), this.mlModel.getAttributes());
            this.blockView.addBlock(block); // Add the ML Model to the graph on the screen.
            this.fabAddDA.setClickable(false);
            dialog.dismiss();
        });

        btnRandomForestRegressor.setOnClickListener(view -> {
            createRandomForestConfigDialog(false);

            dialog.dismiss();
        });

        btnLinearRegression.setOnClickListener(view -> {
            this.mlModel = new MLModel(context.getString(R.string.linear_regression), attributes);
            Block block = new Block(context.getString(R.string.ml) + ": " + this.mlModel.getType(), this.mlModel.getAttributes());
            this.blockView.addBlock(block); // Add the ML Model to the graph on the screen.
            this.fabAddDA.setClickable(false);
            dialog.dismiss();
        });

        btnSVR.setOnClickListener(view -> {
            createSVMConfigDialog(false);
            dialog.dismiss();
        });

        btnElasticNetCV.setOnClickListener(view -> {
            this.mlModel = new MLModel(context.getString(R.string.elastic_net_cv), attributes);
            Block block = new Block(context.getString(R.string.ml) + ": " + this.mlModel.getType(), this.mlModel.getAttributes());
            this.blockView.addBlock(block); // Add the ML Model to the graph on the screen.
            this.fabAddDA.setClickable(false);
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * This function creates a dialog to config the KNN Classifier parameters.
     * @see R.layout#k_nearest_neighbors_config_dialog
     */
    private void createKNNConfigDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.k_nearest_neighbors_config_dialog);

        Spinner spinnerKNNWeights = dialog.findViewById(R.id.spinnerKNNWeights);
        Spinner spinnerKNNAlgorithm = dialog.findViewById(R.id.spinnerKNNAlgorithm);
        Button btnSelectKNNConfig = dialog.findViewById(R.id.btnSelectKNNConfig);

        setSpinnerItems(spinnerKNNWeights, KNN_WEIGHTS);
        setSpinnerItems(spinnerKNNAlgorithm, KNN_ALGORITHMS);

        btnSelectKNNConfig.setOnClickListener(view -> {
            HashMap<String, Object> attributes = new HashMap<>();

            String selectedKNNWeights = (String) spinnerKNNWeights.getItemAtPosition(spinnerKNNWeights.getSelectedItemPosition());
            String selectedKNNAlgorithm = (String) spinnerKNNAlgorithm.getItemAtPosition(spinnerKNNAlgorithm.getSelectedItemPosition()); // Get the config parameters for the ML Model

            attributes.put(context.getString(R.string.weights), selectedKNNWeights);
            attributes.put(context.getString(R.string.algorithm), selectedKNNAlgorithm);

            this.mlModel = new MLModel(context.getString(R.string.k_nearest_neighbors_classifier), attributes);
            Block block = new Block(context.getString(R.string.ml) + ": " + this.mlModel.getType(), this.mlModel.getAttributes());
            this.blockView.addBlock(block); // Add the ML Model to the graph on the screen.

            this.fabAddDA.setClickable(false);
            dialog.dismiss();
        });
        dialog.show();
    }

    /**
     * This function creates a dialog to config the Naive Bayes model
     * @see R.layout#naive_bayes_config_dialog
     */
    private void createNaiveBayesConfigDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.naive_bayes_config_dialog);

        RadioGroup rgNaiveBayes = dialog.findViewById(R.id.rgNaiveBayes);
        Button btnSelectNaiveBayesConfig = dialog.findViewById(R.id.btnSelectNaiveBayesConfig);

        btnSelectNaiveBayesConfig.setOnClickListener(view -> {
            int id = rgNaiveBayes.getCheckedRadioButtonId(); // Get the id of the checked radio button.
            if (id == -1) // If no radio was checked the default model will be selected.
                this.mlModel = new MLModel(context.getString(R.string.gaussian_nb),new HashMap<>());
            else {
                RadioButton rb = dialog.findViewById(id); // Get the selected radio button.

                HashMap<String, Object> attributes = new HashMap<>();

                this.mlModel = new MLModel(rb.getText().toString(), attributes);
                Block block = new Block(context.getString(R.string.ml) + ": " + this.mlModel.getType(), this.mlModel.getAttributes());
                this.blockView.addBlock(block); // Add the ML Model to the graph on the screen.
            }
            this.fabAddDA.setClickable(false);
            dialog.dismiss();
        });
        dialog.show();
    }

    /**
     * This function creates a dialog to config the Random Forest model. This function will distinguish between two types of Random Forest Models:
     * Random Forest Classifier and Random Forest Regressor.
     * @param isClassification <code>true</code> if the dialog is was invoked from clicking {@link R.id#btnRandomForestClassifier},
     * <code>false</code> if it was invoked form{@link R.id#btnRandomForestRegressor}. Random Forest Classifier and Random Forest Regressor
     * have different possible values for the argument <code>criterion</code>, therefore we must distinguish between them and set
     * {@link R.id#spinnerRFCriterion} items to be different.
     * @see R.layout#random_forest_config_dialog
     */
    private void createRandomForestConfigDialog(boolean isClassification) {
        Dialog dialog = new Dialog(context);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.random_forest_config_dialog);

        TextView tvMaxFeatures = dialog.findViewById(R.id.tvMaxFeatures);
        SeekBar skBarRFNEstimators = dialog.findViewById(R.id.skBarRFNEstimators);
        Spinner spinnerRFMaxFeatures = dialog.findViewById(R.id.spinnerRFMaxFeatures);
        Spinner spinnerRFCriterion = dialog.findViewById(R.id.spinnerRFCriterion);
        Button btnSelectRFConfig = dialog.findViewById(R.id.btnSelectRFConfig);

        skBarRFNEstimators.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvMaxFeatures.setText(context.getString(R.string.n_estimators) + " " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        setSpinnerItems(spinnerRFMaxFeatures, RANDOM_FOREST_MAX_FEATURES);

        if (isClassification) // Assign the Random Forest Classifier criterion options to the spinner.
            setSpinnerItems(spinnerRFCriterion, RANDOM_FOREST_CLASSIFIER_CRITERIA);
        else // Assign the Random Forest Regressor criterion options to the spinner.
            setSpinnerItems(spinnerRFCriterion, RANDOM_FOREST_REGRESSOR_CRITERIA);

        btnSelectRFConfig.setOnClickListener(v -> {
            HashMap<String, Object> attributes = new HashMap<>();

            String maxFeatures = (String) spinnerRFMaxFeatures.getItemAtPosition(spinnerRFMaxFeatures.getSelectedItemPosition());
            String criterion = (String) spinnerRFCriterion.getItemAtPosition(spinnerRFCriterion.getSelectedItemPosition());

            attributes.put(context.getString(R.string.n_estimators_param), skBarRFNEstimators.getProgress());
            attributes.put(context.getString(R.string.max_features), maxFeatures);
            attributes.put(context.getString(R.string.criterion), criterion); // Set the configuration parameters.

            if (isClassification) // Distinguish between Random Forest Classifier and Random Forest Regressor.
                this.mlModel = new MLModel(context.getString(R.string.random_forest_classifier), attributes);
            else
                this.mlModel = new MLModel(context.getString(R.string.random_forest_regressor), attributes);

            Block block = new Block(context.getString(R.string.ml) + ": " + this.mlModel.getType(), this.mlModel.getAttributes());
            this.blockView.addBlock(block); // Add the ML Model to the graph on the screen.

            this.fabAddDA.setClickable(false);
            dialog.dismiss();

        });
        dialog.show();
    }

    /**
     * This function creates a dialog to config the SVM model.
     * @param isClassification <code>true</code> if the dialog is was invoked from clicking {@link R.id#btnSVC},
     * <code>false</code> if it was invoked form{@link R.id#btnSVR}. This variable is used to distinguish between the models.
     * @see R.layout#svm_config_dialog
     */
    private void createSVMConfigDialog(boolean isClassification) {
        Dialog dialog = new Dialog(context);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.svm_config_dialog);

        Spinner spinnerSVMKernel = dialog.findViewById(R.id.spinnerSVMKernel);
        TextView tvSVMDegree = dialog.findViewById(R.id.tvSVMDegree);
        SeekBar skBarSVMDegree = dialog.findViewById(R.id.skBarSVMDegree);
        Button btnSelectSVMConfig = dialog.findViewById(R.id.btnSelectSVMConfig);

        setSpinnerItems(spinnerSVMKernel, SVM_KERNEL);

        skBarSVMDegree.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSVMDegree.setText(context.getString(R.string.degree) + " " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btnSelectSVMConfig.setOnClickListener(v -> {
            HashMap<String, Object> attributes = new HashMap<>();

            String kernel = (String) spinnerSVMKernel.getItemAtPosition(spinnerSVMKernel.getSelectedItemPosition());

            attributes.put(context.getString(R.string.kernel), kernel);
            attributes.put(context.getString(R.string.degree_param), skBarSVMDegree.getProgress()); // Set the configuration parameters.

            if (isClassification) // Distinguish between SVC and SVR.
                this.mlModel = new MLModel(context.getString(R.string.svc), attributes);
            else
                this.mlModel = new MLModel(context.getString(R.string.svr), attributes);

            Block block = new Block(context.getString(R.string.ml) + ": " + this.mlModel.getType(), this.mlModel.getAttributes());
            this.blockView.addBlock(block); // Add the ML Model to the graph on the screen.

            this.fabAddDA.setClickable(false);
            dialog.dismiss();
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

    public String getyColumn() {
        return this.yColumn;
    }

    public MLModel getMlModel() {
        return this.mlModel;
    }

    /**
     * This function undo the creation of ML Model.
     */
    public void undo() {
        this.mlModel = null;
        this.blockView.removeBlock();
    }
}

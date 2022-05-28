package com.automl.automl;
// TODO - make sure that the user didn't block notifications.
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.HashMap;

public class TestMLModelActivity extends AppCompatActivity {

    private TextView tvMLModelScore;private Button btnTest;
    private LinearLayout llTestMLModel;
    private TextView tvResult;

    public static final String EDIT_TEXT_PREFIX = "etTestMLModel"; // The prefix for the test ml model edit texts.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mlmodel);

        tvMLModelScore = findViewById(R.id.tvMLModelScore);
        btnTest = findViewById(R.id.btnTest);
        llTestMLModel = findViewById(R.id.llTestMLModel);
        tvResult = findViewById(R.id.tvResult);

        Intent intent = getIntent();
        MLTest mlTest = (MLTest) intent.getSerializableExtra(MLPipelineService.ML_TEST);

        String score = mlTest.getScore();

        tvMLModelScore.setText(tvMLModelScore.getText().toString() + score);

        createTestLayout(mlTest);

        btnTest.setOnClickListener(v -> {
            double[] testingData = getTestingData(mlTest);

            if (testingData == null) // If the testing data is empty.
                return;

            if (!Python.isStarted())
                Python.start(new AndroidPlatform(this));

            Python py = Python.getInstance();
            PyObject result = py.getModule("main").callAttr("test_ml", testingData, mlTest.getMlModel(), mlTest.getyColumnEncoding());

            tvResult.setText("Result: " + result.toString());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test_ml_model_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemGoHome) { // If the user is done testing the ML Model they will be returned to the home activity.
            Intent intent = new Intent(TestMLModelActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This function creates a layout with text views and edit texts where the user will be able to
     * try different data and predict the result for the data.
     * @param mlTest The information about the dataset.
     */
    private void createTestLayout(MLTest mlTest) {
        String[] columns = mlTest.getColumns();
        HashMap<String, double[]> normalizationInfo = mlTest.getNormalizationInfo();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (String column : columns) {
            double[] arr = normalizationInfo.get(column); // The normalization info of this column.

            TextView tvTitle = new TextView(this);
            tvTitle.setLayoutParams(params);
            tvTitle.setText("Column: " + column);
            tvTitle.setTextSize(20);


            TextView tvMinMaxInfo = new TextView(this);
            tvMinMaxInfo.setLayoutParams(params);
            tvMinMaxInfo.setText("Min Value: " + arr[0] + "\nMax Value: " + arr[1]);

            EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
            et.setHint("Enter " + column);
            et.setId((EDIT_TEXT_PREFIX + column).hashCode());

            llTestMLModel.addView(tvTitle);
            llTestMLModel.addView(tvMinMaxInfo);
            llTestMLModel.addView(et);
        }
    }

    /**
     * This function clears all the fields created in {@link #createTestLayout(MLTest)}.
     * @param columns The columns in the dataset. Used to identify the {@link EditText}s.
     */
    private void clearAllFields(String[] columns) {
        for (String column : columns) {
            EditText et = findViewById((EDIT_TEXT_PREFIX + column).hashCode());
            et.setText("");
        }
    }

    /**
     * This function gathers all the data inserted in the fields created in {@link #createTestLayout(MLTest)}
     * and normalizes the data.
     * @param mlTest The information about the dataset.
     * @return the prepared data.
     */
    private double[] getTestingData(MLTest mlTest) {
        String[] columns = mlTest.getColumns();

        double[] values = new double[columns.length];

        HashMap<String, double[]> normalizationInfo = mlTest.getNormalizationInfo();

        for (int i = 0; i < columns.length; i++) {
            double[] arr = normalizationInfo.get(columns[i]);

            assert arr != null;

            double min = arr[0];
            double max = arr[1];

            EditText et = findViewById((EDIT_TEXT_PREFIX + columns[i]).hashCode());

            String s = et.getText().toString(); // The value for this column.

            if (s.length() == 0) { // If the string is empty then the user hasn't inserted any value. Therefore the
                Toast.makeText(TestMLModelActivity.this, "One or more of the fields are empty. Please fill them.", Toast.LENGTH_SHORT).show();
                return null;
            }
            else { // Normalizing the value
                double d = Double.parseDouble(s);
                d = (d - min) / (max - min);
                d *= (max - min);
                d += min;
                values[i] = d;
            }
        }
        return values;
    }
}
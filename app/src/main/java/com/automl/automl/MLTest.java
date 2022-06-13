package com.automl.automl;

import android.net.Uri;

import com.chaquo.python.PyObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class is used to test the ML Model.
 * The class will have all the required attributes to test the ML Model.
 * This class is used since we need to pass all the values via an intent,
 * but an intent only takes extras that implement the {@link Serializable} interface.
 */
public class MLTest implements Serializable {

    private String score; // The score of the ML Model.

    /**
     * The encoding of the y column in a python object format - dict.
     * If this is a regression task, the encoding will be an empty dictionary.
     */
    private static PyObject yColumnEncoding;

    /**
     * The ML Model that was built in the ML Mode building process.
     */
    private static PyObject mlModel;

    /**
     * The normalization info for each column.
     * This is necessary because the new information will not be normalized,
     * and it should be normalized so that the ML Model will perform the best.
     */
    private HashMap<String, double[]> normalizationInfo = new HashMap<>();
    private String[] columns; // The columns in the dataset.

    public MLTest(String yColumn, String score, PyObject yColumnEncoding, PyObject mlModel, PyObject normalizationInfo, List<PyObject> columns) {
        this.score = "" + (Double.parseDouble(score) * 100) + "%";
        MLTest.yColumnEncoding = yColumnEncoding;
        MLTest.mlModel = mlModel;
        Map<PyObject, PyObject> map = normalizationInfo.asMap();

        for (PyObject py : map.keySet()) {
            List<PyObject> lst = Objects.requireNonNull(map.get(py)).asList();
            double[] arr = new double[] {lst.get(0).toDouble(), lst.get(1).toDouble()};
            this.normalizationInfo.put(py.toString(), arr);
        }

        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < columns.size(); i++) {
            if (!columns.get(i).toString().equals(yColumn))
                list.add(columns.get(i).toString());
        }

        this.columns = list.toArray(new String[0]);
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public PyObject getyColumnEncoding() {
        return yColumnEncoding;
    }

    public void setyColumnEncoding(PyObject yColumnEncoding) {
        MLTest.yColumnEncoding = yColumnEncoding;
    }

    public PyObject getMlModel() {
        return mlModel;
    }

    public void setMlModel(PyObject mlModel) {
        MLTest.mlModel = mlModel;
    }

    public HashMap<String, double[]> getNormalizationInfo() {
        return normalizationInfo;
    }

    public void setNormalizationInfo(HashMap<String, double[]> normalizationInfo) {
        this.normalizationInfo = normalizationInfo;
    }

    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return "MLTest{" +
                "score='" + score + '\'' +
                ", normalizationInfo=" + normalizationInfo +
                ", columns=" + Arrays.toString(columns) +
                '}';
    }
}

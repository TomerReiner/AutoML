package com.automl.automl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This is an adapter class for the list view in {@link MyModelsActivity}.
 * @see R.layout#ml_model_item_list_view
 * @see MyModelsActivity
 */
public class MyModelsListViewAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<MLModelDisplay> models;

    public MyModelsListViewAdapter(Context context, ArrayList<MLModelDisplay> models) {
        this.context = context;
        this.models = models;
    }

    @Override
    public int getCount() {
        return this.models.size();
    }

    @Override
    public Object getItem(int i) {
        return this.models.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.ml_model_item_list_view, viewGroup, false);

        TextView tvMLModel = v.findViewById(R.id.tvMLModel);

        MLModelDisplay model = this.models.get(i);

        tvMLModel.setText(model.toString());

        return v;
    }
}

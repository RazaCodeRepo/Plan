package com.example.kontrol.plan.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kontrol.plan.R;
import com.example.kontrol.plan.model.Plan;

import java.util.List;

/**
 * Created by kontrol on 1/28/2018.
 */

public class GroupAdapter extends ArrayAdapter<Plan> {

    public GroupAdapter(Context context, int resource, List<Plan> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.plan_list_item, parent, false);
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.tv_group_name);

        Plan plan = getItem(position);

        nameTextView.setText(plan.getName());

        return convertView;
    }
}
